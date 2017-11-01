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

package com.vmware.vm;

import com.vmware.common.annotations.Action;
import com.vmware.common.annotations.Option;
import com.vmware.common.annotations.Sample;
import com.vmware.connection.ConnectedVimServiceBase;
import com.vmware.vim25.*;

import java.util.*;

/**
 * <pre>
 * VMSnapshot
 *
 * This sample demonstrates VM snapshot operations
 *
 * <b>Parameters:</b>
 * url            [required] : url of the web service.
 * username       [required] : username for the authentication
 * password       [required] : password for the authentication
 * vmname         [required] : Name of the virtual machine
 * operation      [required] : operation type - [list|create|remove|revert]
 * snapshotname   [optional] : Name of the snapshot
 * description    [optional] : description of the sanpshot
 * removechild    [optional] : remove snapshot children - [1 | 0]
 *
 * <b>Command Line:</b>
 * List VM snapshot names
 * run.bat com.vmware.vm.VMSnapshot
 * --url [webserviceurl] --username [username] --password  [password]
 * --vmname [vmname] --operation list
 *
 * Create VM snapshot
 * run.bat com.vmware.vm.VMSnapshot
 * --url [webserviceurl] --username [username] --password  [password]
 * --vmname [vmname] --operation create
 * --description [Description of the snapshot]
 *
 * Revert VM snapshot
 * run.bat com.vmware.vm.VMSnapshot
 * --url [webserviceurl] --username [username] --password  [password]
 * --vmname [vmname] --operation revert --description [Snapshot Description]
 *
 * Remove VM snapshot
 * run.bat com.vmware.vm.VMSnapshot
 * --url [webserviceurl] --username [username] --password  [password]
 * --vmname [vmname] --operation remove --removechild 0
 * </pre>
 */

@Sample(
        name = "vm-snapshot",
        description = "This sample demonstrates VM snapshot operations"
)
public class VMSnapshot extends ConnectedVimServiceBase {

    String virtualMachineName;
    String operation;
    String snapshotname;
    String description;
    String removechild;

    @Option(name = "vmname", description = "Name of the virtual machine")
    public void setVirtualMachineName(String virtualMachineName) {
        this.virtualMachineName = virtualMachineName;
    }

    @Option(name = "operation", description = "operation type - [list|create|remove|revert]")
    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Option(name = "snapshotname", required = false, description = "Name of the snapshot")
    public void setSnapshotname(String snapshotname) {
        this.snapshotname = snapshotname;
    }

    @Option(name = "description", required = false, description = "description of the sanpshot")
    public void setDescription(String description) {
        this.description = description;
    }

    @Option(name = "removechild", required = false, description = "remove snapshot children - [1 | 0]")
    public void setRemovechild(String removechild) {
        this.removechild = removechild;
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
        propertySpec.setAll(false);
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
        objectSpec.setSkip(true);
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

    boolean createSnapshot(ManagedObjectReference vmMor)
            throws FileFaultFaultMsg, InvalidNameFaultMsg, InvalidStateFaultMsg,
            RuntimeFaultFaultMsg, SnapshotFaultFaultMsg, TaskInProgressFaultMsg,
            VmConfigFaultFaultMsg, InvalidPropertyFaultMsg,
            InvalidCollectorVersionFaultMsg {
        ManagedObjectReference taskMor =
                vimPort.createSnapshotTask(vmMor, snapshotname, description, true,
                        true);
        if (getTaskResultAfterDone(taskMor)) {
            System.out.printf(" Creating Snapshot - [ %s ] Successful %n",
                    snapshotname);
            return true;
        } else {
            System.out.printf(" Creating Snapshot - [ %s ] Failure %n",
                    snapshotname);
            return false;
        }
    }

    boolean listSnapshot(ManagedObjectReference vmMor)
            throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
        VirtualMachineSnapshotInfo snapInfo =
                (VirtualMachineSnapshotInfo) getMOREFs.entityProps(vmMor,
                        new String[]{"snapshot"}).get("snapshot");
        if (snapInfo == null) {
            System.out.println("No Snapshots found");
        } else {
            List<VirtualMachineSnapshotTree> listvmsht =
                    snapInfo.getRootSnapshotList();
            traverseSnapshotInTree(listvmsht, null, true);
        }
        return true;
    }

    boolean revertSnapshot(ManagedObjectReference vmMor) throws RuntimeFaultFaultMsg, TaskInProgressFaultMsg, VmConfigFaultFaultMsg, InsufficientResourcesFaultFaultMsg, FileFaultFaultMsg, InvalidStateFaultMsg, InvalidPropertyFaultMsg, InvalidCollectorVersionFaultMsg {
        ManagedObjectReference snapmor =
                getSnapshotReference(vmMor, virtualMachineName, snapshotname);
        if (snapmor != null) {
            ManagedObjectReference taskMor =
                    vimPort.revertToSnapshotTask(snapmor, null, true);
            if (getTaskResultAfterDone(taskMor)) {
                System.out.printf(" Reverting Snapshot - [ %s ] Successful %n",
                        snapshotname);
                return true;
            } else {
                System.out.printf(" Reverting Snapshot - [ %s ] Failure %n",
                        snapshotname);
                return false;
            }
        } else {
            System.out.println("Snapshot not found");
        }
        return false;
    }

    boolean removeAllSnapshot(ManagedObjectReference vmMor) throws RuntimeFaultFaultMsg, TaskInProgressFaultMsg, SnapshotFaultFaultMsg, InvalidStateFaultMsg, InvalidPropertyFaultMsg, InvalidCollectorVersionFaultMsg {
        ManagedObjectReference taskMor =
                vimPort.removeAllSnapshotsTask(vmMor, true);
        if (taskMor != null) {
            String[] opts =
                    new String[]{"info.state", "info.error", "info.progress"};
            String[] opt = new String[]{"state"};
            Object[] results =
                    waitForValues.wait(taskMor, opts, opt, new Object[][]{new Object[]{
                            TaskInfoState.SUCCESS, TaskInfoState.ERROR}});

            // Wait till the task completes.
            if (results[0].equals(TaskInfoState.SUCCESS)) {
                System.out.printf(
                        " Removing All Snapshots on - [ %s ] Successful %n",
                        virtualMachineName);
                return true;
            } else {
                System.out.printf(" Removing All Snapshots on - [ %s ] Failure %n",
                        virtualMachineName);
                return false;
            }
        } else {
            return false;
        }
    }

    boolean removeSnapshot(ManagedObjectReference vmMor) throws RuntimeFaultFaultMsg, InvalidPropertyFaultMsg, InvalidCollectorVersionFaultMsg, TaskInProgressFaultMsg {
        int rem = Integer.parseInt(removechild);
        boolean flag = true;
        if (rem == 0) {
            flag = false;
        }
        ManagedObjectReference snapmor =
                getSnapshotReference(vmMor, virtualMachineName, snapshotname);
        if (snapmor != null) {
            ManagedObjectReference taskMor =
                    vimPort.removeSnapshotTask(snapmor, flag, true);
            if (taskMor != null) {
                String[] opts =
                        new String[]{"info.state", "info.error", "info.progress"};
                String[] opt = new String[]{"state"};
                Object[] results =
                        waitForValues.wait(taskMor, opts, opt,
                                new Object[][]{new Object[]{TaskInfoState.SUCCESS,
                                        TaskInfoState.ERROR}});

                // Wait till the task completes.
                if (results[0].equals(TaskInfoState.SUCCESS)) {
                    System.out.printf(" Removing Snapshot - [ %s ] Successful %n",
                            snapshotname);
                    return true;
                } else {
                    System.out.printf(" Removing Snapshot - [ %s ] Failure %n",
                            snapshotname);
                    return false;
                }
            }
        } else {
            System.out.println("Snapshot not found");
        }
        return false;
    }

    ManagedObjectReference traverseSnapshotInTree(
            List<VirtualMachineSnapshotTree> snapTree, String findName,
            boolean print) {
        ManagedObjectReference snapmor = null;
        if (snapTree == null) {
            return snapmor;
        }
        for (VirtualMachineSnapshotTree node : snapTree) {
            if (print) {
                System.out.println("Snapshot Name : " + node.getName());
            }
            if (findName != null && node.getName().equalsIgnoreCase(findName)) {
                return node.getSnapshot();
            } else {
                List<VirtualMachineSnapshotTree> listvmst =
                        node.getChildSnapshotList();
                List<VirtualMachineSnapshotTree> childTree = listvmst;
                snapmor = traverseSnapshotInTree(childTree, findName, print);
            }
        }
        return snapmor;
    }

    ManagedObjectReference getSnapshotReference(
            ManagedObjectReference vmmor, String vmName, String snapName) throws RuntimeFaultFaultMsg, InvalidPropertyFaultMsg {
        VirtualMachineSnapshotInfo snapInfo =
                (VirtualMachineSnapshotInfo) getMOREFs.entityProps(vmmor,
                        new String[]{"snapshot"}).get("snapshot");
        ManagedObjectReference snapmor = null;
        if (snapInfo != null) {
            List<VirtualMachineSnapshotTree> listvmst =
                    snapInfo.getRootSnapshotList();
            snapmor = traverseSnapshotInTree(listvmst, snapName, false);
            if (snapmor == null) {
                System.out.println("No Snapshot named : " + snapName
                        + " found for VirtualMachine : " + vmName);
            }
        } else {
            System.out
                    .println("No Snapshots found for VirtualMachine : " + vmName);
        }
        return snapmor;
    }

    boolean isOptionSet(String test) {
        return (test == null) ? false : true;
    }
    //判断输入正确与否
    boolean verifyInputArguments() {
        boolean flag = true;
        String op = operation;
        if (op.equalsIgnoreCase("create")) {
            if ((!isOptionSet(snapshotname)) || (!isOptionSet(description))) {
                System.out.println("For Create operation SnapshotName"
                        + " and Description are the Mandatory options");
                flag = false;
            }
        }
        if (op.equalsIgnoreCase("remove")) {
            if ((!isOptionSet(snapshotname)) || (!isOptionSet(removechild))) {
                System.out.println("For Remove operation Snapshotname"
                        + " and removechild are the Mandatory option");
                flag = false;
            } else {
                int child = Integer.parseInt(removechild);
                if (child != 0 && child != 1) {
                    System.out.println("Value of removechild parameter"
                            + " must be either 0 or 1");
                    flag = false;
                }
            }
        }
        if (op.equalsIgnoreCase("revert")) {
            if ((!isOptionSet(snapshotname))) {
                System.out.println("For Revert operation SnapshotName"
                        + " is the Mandatory option");
                flag = false;
            }
        }
        return flag;
    }

    @Action
    public void run() throws InvalidPropertyFaultMsg, TaskInProgressFaultMsg, SnapshotFaultFaultMsg, VmConfigFaultFaultMsg, FileFaultFaultMsg, InvalidStateFaultMsg, InvalidCollectorVersionFaultMsg, InvalidNameFaultMsg, RuntimeFaultFaultMsg, InsufficientResourcesFaultFaultMsg {
        boolean valid = false;
        valid = verifyInputArguments();
        if (!valid) {
            return;
        }
        ManagedObjectReference vmRef =
                getMOREFsInContainerByType(serviceContent.getRootFolder(),
                        "VirtualMachine").get(virtualMachineName);
       
        

        if (vmRef != null) {
            boolean res = false;
            if (operation.equalsIgnoreCase("create")) {
                res = createSnapshot(vmRef);
            } else if (operation.equalsIgnoreCase("list")) {
                res = listSnapshot(vmRef);
            } else if (operation.equalsIgnoreCase("revert")) {
                res = revertSnapshot(vmRef);
            } else if (operation.equalsIgnoreCase("removeall")) {
                res = removeAllSnapshot(vmRef);
            } else if (operation.equalsIgnoreCase("remove")) {
                res = removeSnapshot(vmRef);
            } else {
                System.out.println("Invalid operation [create|list|"
                        + "revert|removeall|remove]");
            }
            if (res) {
                System.out.println("Operation " + operation
                        + "snapshot completed sucessfully");
            }
        } else {
            System.out.println("Virtual Machine " + virtualMachineName
                    + " not found.");
            return;
        }
    }
}
