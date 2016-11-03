package com.biobam.blast2go.apps.submitter.action;

import java.util.EnumSet;
import java.util.Set;

import com.biobam.blast2go.api.action.ActionType;
import com.biobam.blast2go.api.action.B2GAction;
import com.biobam.blast2go.api.action.IB2GBaseGroup;
import com.biobam.blast2go.api.action.PlaceTag;
import com.biobam.blast2go.api.action.icon.DefaultB2GIcons;
import com.biobam.blast2go.api.action.icon.IB2GIcon;
import com.biobam.blast2go.api.action.internal.IB2GPermissionHandler;
import com.biobam.blast2go.api.user.IUserInfoConstants;
import com.biobam.blast2go.api.user.Profile;
import com.biobam.blast2go.api.wizard.B2GWizard;
import com.biobam.blast2go.apps.submitter.job.SubmitterCompleteJobMetadata;
import com.biobam.blast2go.apps.submitter.wizard.SubmitterCompleteWizard;


public class SubmitterActionCompleteGenome extends B2GAction<SubmitterCompleteJobMetadata>
		implements IB2GPermissionHandler {

	@Override
	public IB2GBaseGroup getActionGroup() {
		// TODO Auto-generated method stub
		return IB2GBaseGroup.BASE_GROUP;
	}


	@Override
	public IB2GIcon getActionIcon() {

		// TODO Auto-generated method stub
        return DefaultB2GIcons.PRO_ICON;	}

	@Override
	public ActionType getActionType() {
		// TODO Auto-generated method stub
		return ActionType.RUN;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "com.blast2go.biobam.submitter.action";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		// return "Submit annotated ";
		return "Create NCBI GenBank Genome Submission Files (online)";
	}

	@Override
	public Set<PlaceTag> getPlaceTags() {
		return EnumSet.of(PlaceTag.MENU_TOOLS);
	}

	@Override
	public int getPreferredPositionInMenu() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public Class<? extends B2GWizard<?>> getWizardClass() {
		// TODO Auto-generated method stub
		return SubmitterCompleteWizard.class;
	}

	@Override
	public Class<SubmitterCompleteJobMetadata> jobMetadataClass() {
		// TODO Auto-generated method stub
		return SubmitterCompleteJobMetadata.class;
	}

	@Override
	public EnumSet<Profile> executionPermissions() {
		return IUserInfoConstants.PRO_USERS_GROUP;
	}

}
