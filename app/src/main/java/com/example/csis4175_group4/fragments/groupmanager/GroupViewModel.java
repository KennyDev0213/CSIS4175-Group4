package com.example.csis4175_group4.fragments.groupmanager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GroupViewModel extends ViewModel {

    private MutableLiveData<Group> selectedGroup = new MutableLiveData<>();
    private MutableLiveData<String> selectedGroupId = new MutableLiveData<>();
    private boolean isGroupOwner;

    public LiveData<Group> getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(Group group) {
        selectedGroup.setValue(group);
    }

    public LiveData<String> getSelectedGroupId() {
        return selectedGroupId;
    }

    public void setSelectedGroupId(String groupId) {
        selectedGroupId.setValue(groupId);
    }

    public boolean isGroupOwner() {
        return isGroupOwner;
    }

    public void setIsGroupOwner(boolean value) {
        isGroupOwner = value;
    }
}
