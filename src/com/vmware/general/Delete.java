/*
 * ******************************************************
 * Copyright VMware, Inc. 2010-2012.  All Rights Reserved.
 * ******************************************************
 *
 * DISCLAIMER. THIS PROGRAM IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTIES OR CONDITIONS # OF ANY KIND, WHETHER ORAL OR WRITTEN,
 * EXPRESS OR IMPLIED. THE AUTHOR SPECIFICALLY # DISCLAIMS ANY IMPLIED
 * WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY # QUALITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE.
 */

package com.vmware.general;

import com.vmware.common.annotations.Action;
import com.vmware.common.annotations.Option;
import com.vmware.common.annotations.Sample;
import com.vmware.connection.ConnectedVimServiceBase;
import com.vmware.vim25.*;

import java.rmi.RemoteException;
import java.util.*;


/**
 * <pre>
 * Delete
 *
 * This sample deletes the specified managed entity from the inventory tree
 * The managed entity can be a virtual machine, ClusterComputeResource or a Folder
 *
 * <b>Parameters</b>
 * url          [required] : url of the web service
 * username     [required] : username for the authentication
 * password     [required] : password for the authentication
 * entityname   [required] : Virtual Machine|ClusterComputeResource|Folder
 *
 * <b>Command Line:</b>
 * To delete a folder named 'testFolder'
 * run.bat com.vmware.general.Delete --url [webserviceurl]
 * --username [username] --password [password] --entityname [testFolder]
 *
 * To delete a datacenter named myData
 * run.bat com.vmware.general.Delete --url [webserviceurl]
 * --username [username] --password [password] --entityname [myData]
 * </pre>
 */
@Sample(
        name = "delete",
        description = "This sample deletes the specified managed entity from the inventory tree " +
                "The managed entity can be a virtual machine, ClusterComputeResource or a Folder."
)
public class Delete extends ConnectedVimServiceBase {
    private String managedEntityName;

    @Option(name = "entityname", description = "name of entity to delete")
    public void setManagedEntityName(String name) {
        this.managedEntityName = name;
    }

    /**
     * Returns all the MOREFs of the specified type that are present under the
     * container
     *
     * @param folder    {@link ManagedObjectReference} of the container to begin the
     *                  search from
     * @param morefType Type of the managed entity that needs to be searched
     * @return Map of name and MOREF of the managed objects present. If none
     *         exist then empty Map is returned
     * @throws InvalidPropertyFaultMsg
     * @throws RuntimeFaultFaultMsg
     */
    Map<String, ManagedObjectReference> getMOREFsInContainerByType(
            ManagedObjectReference folder, String morefType)
            throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
        String PROP_ME_NAME = "name";
        ManagedObjectReference viewManager = serviceContent.getViewManager();
        ManagedObjectReference containerView =
                vimPort.createContainerView(viewManager, folder,
                        Arrays.asList(morefType), true);

        Map<String, ManagedObjectReference> tgtMoref =
                new HashMap<String, ManagedObjectReference>();

        // Create Property Spec
        PropertySpec propertySpec = new PropertySpec();
        propertySpec.setAll(Boolean.FALSE);
        propertySpec.setType(morefType);
        propertySpec.getPathSet().add(PROP_ME_NAME);

        TraversalSpec ts = new TraversalSpec();
        ts.setName("view");
        ts.setPath("view");
        ts.setSkip(false);
        ts.setType("ContainerView");

        // Now create Object Spec
        ObjectSpec objectSpec = new ObjectSpec();
        objectSpec.setObj(containerView);
        objectSpec.setSkip(Boolean.TRUE);
        objectSpec.getSelectSet().add(ts);

        // Create PropertyFilterSpec using the PropertySpec and ObjectPec
        // created above.
        PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
        propertyFilterSpec.getPropSet().add(propertySpec);
        propertyFilterSpec.getObjectSet().add(objectSpec);

        List<PropertyFilterSpec> propertyFilterSpecs =
                new ArrayList<PropertyFilterSpec>();
        propertyFilterSpecs.add(propertyFilterSpec);

        RetrieveResult rslts =
                vimPort.retrievePropertiesEx(serviceContent.getPropertyCollector(),
                        propertyFilterSpecs, new RetrieveOptions());
        List<ObjectContent> listobjcontent = new ArrayList<ObjectContent>();
        if (rslts != null && rslts.getObjects() != null
                && !rslts.getObjects().isEmpty()) {
            listobjcontent.addAll(rslts.getObjects());
        }
        String token = null;
        if (rslts != null && rslts.getToken() != null) {
            token = rslts.getToken();
        }
        while (token != null && !token.isEmpty()) {
            rslts =
                    vimPort.continueRetrievePropertiesEx(
                            serviceContent.getPropertyCollector(), token);
            token = null;
            if (rslts != null) {
                token = rslts.getToken();
                if (rslts.getObjects() != null && !rslts.getObjects().isEmpty()) {
                    listobjcontent.addAll(rslts.getObjects());
                }
            }
        }
        for (ObjectContent oc : listobjcontent) {
            ManagedObjectReference mr = oc.getObj();
            String entityNm = null;
            List<DynamicProperty> dps = oc.getPropSet();
            if (dps != null) {
                for (DynamicProperty dp : dps) {
                    entityNm = (String) dp.getVal();
                }
            }
            tgtMoref.put(entityNm, mr);
        }
        return tgtMoref;
    }

    /**
     * This method returns a boolean value specifying whether the Task is
     * succeeded or failed.
     *
     * @param task ManagedObjectReference representing the Task.
     * @return boolean value representing the Task result.
     * @throws InvalidCollectorVersionFaultMsg
     *
     * @throws RuntimeFaultFaultMsg
     * @throws InvalidPropertyFaultMsg
     */
    boolean getTaskResultAfterDone(ManagedObjectReference task)
            throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg,
            InvalidCollectorVersionFaultMsg {

        boolean retVal = false;

        // info has a property - state for state of the task
        Object[] result =
                waitForValues.wait(task, new String[]{"info.state", "info.error"},
                        new String[]{"state"}, new Object[][]{new Object[]{
                        TaskInfoState.SUCCESS, TaskInfoState.ERROR}});

        if (result[0].equals(TaskInfoState.SUCCESS)) {
            retVal = true;
        }
        if (result[1] instanceof LocalizedMethodFault) {
            throw new RuntimeException(
                    ((LocalizedMethodFault) result[1]).getLocalizedMessage());
        }
        return retVal;
    }

    @Action
    public void deleteManagedEntity() throws RuntimeFaultFaultMsg,
            RemoteException, InvalidPropertyFaultMsg, VimFaultFaultMsg, InvalidCollectorVersionFaultMsg {

        Map<String, ManagedObjectReference> entities =
                getMOREFsInContainerByType(serviceContent.getRootFolder(),
                        "ManagedEntity");

        ManagedObjectReference manobjref = entities.get(managedEntityName);
        if (manobjref == null) {
            System.out.printf(" Unable to find a Managed Entity By name [ %s ]",
                    managedEntityName);
            return;
        } else {
            ManagedObjectReference taskmor = vimPort.destroyTask(manobjref);
            if (getTaskResultAfterDone(taskmor)) {
                System.out.printf(
                        "Successful delete of Managed Entity Name - [ %s ]"
                                + " and Entity Type - [ %s ]%n", managedEntityName,
                        manobjref.getType());
            }
        }
    }
}