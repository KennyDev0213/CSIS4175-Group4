package com.example.csis4175_group4.fragments.groupmanager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GroupViewModel extends ViewModel {

    private MutableLiveData<Group> selectedGroup = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedGroupId = new MutableLiveData<>();

    public LiveData<Group> getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(Group group) {
        selectedGroup.setValue(group);
    }

    public LiveData<Integer> getSelectedGroupId() {
        return selectedGroupId;
    }

    public void setSelectedGroupId(int groupId) {
        selectedGroupId.setValue(groupId);
    }
}
