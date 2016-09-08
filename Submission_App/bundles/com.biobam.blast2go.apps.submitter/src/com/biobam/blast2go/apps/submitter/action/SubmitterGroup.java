package com.biobam.blast2go.apps.submitter.action;

import java.util.Collections;
import java.util.List;

import com.biobam.blast2go.api.action.IB2GBaseGroup;
import com.biobam.blast2go.api.action.icon.B2GIconImpl;
import com.biobam.blast2go.api.action.icon.IB2GIcon;


public class SubmitterGroup implements IB2GBaseGroup {

	public static SubmitterGroup Instance =  new SubmitterGroup();

	private SubmitterGroup() {

	}
    //private String sIcon = "platform:/plugin/com.biobam.blast2go.Submitter/res/icon_s.jpg";
	@Override
	public IB2GIcon getGroupIcon() {
		return B2GIconImpl.NO_ICON;
	}

	@Override
	public String getGroupId() {
		//set an identifier
		return "com.blast2go.biobam.submitter.action.group";
	}

	@Override
	public String getName() {
		return "Create NCBI GenBank Genome Submission Files";
	}

	@Override
	public IB2GBaseGroup getParentGroup() {
		// The parent must be the base group if this is your main group.
        // For subgroups set your own parent group.
        return IB2GBaseGroup.BASE_GROUP;
	}

	@Override
	 public int getPreferredPositionInMenu() {
        // Preferred position when there is more than one entry in the menu.
        return 10;
    }

    @Override
    public List<Integer> getSeparatorsPrefferedPositions() {
        // Return the positions where you define separators, inside this group.
        return Collections.emptyList();
    }
}

