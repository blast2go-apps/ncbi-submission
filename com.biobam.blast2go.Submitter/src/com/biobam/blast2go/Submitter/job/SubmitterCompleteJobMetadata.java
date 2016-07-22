package com.biobam.blast2go.Submitter.job;

import java.util.List;

import com.biobam.blast2go.api.job.IB2GJobMetadata;
import com.biobam.blast2go.api.job.InputDefinition;
import com.biobam.blast2go.dag.model.IGODag;
import com.biobam.blast2go.dag.model.IGODagConstants;
import com.biobam.blast2go.workbench.services.IB2GFilesDirectory;

import es.blast2go.data.IProject;

public class SubmitterCompleteJobMetadata implements IB2GJobMetadata<SubmitterCompleteJob, SubmitterJobParameters> {
	public static InputDefinition<IGODag> GO_DAG = IGODagConstants.INPUT_DEFINITION;
	public static InputDefinition<IProject> INPUT_PROJECT = new InputDefinition<IProject>(IProject.class, "Iproject",
			"Iproject");
	public static InputDefinition<IB2GFilesDirectory> ADDITIONAL_B2GFILES_DIRECTORY = IB2GFilesDirectory.INPUT_DEFINITION;

	@Override
	public List<InputDefinition<?>> additionalRequirements() {
		return InputDefinition.listOf(GO_DAG, ADDITIONAL_B2GFILES_DIRECTORY);
	}

	@Override
	public List<InputDefinition<?>> inputs() {
		return InputDefinition.listOf(INPUT_PROJECT);
	}

	@Override
	public Class<SubmitterCompleteJob> jobClass() {
		return SubmitterCompleteJob.class;
	}

	@Override
	public List<InputDefinition<?>> outputs() {
		return InputDefinition.EMPTY_LIST;
	}

	@Override
	public Class<SubmitterJobParameters> parametersClass() {
		return SubmitterJobParameters.class;
	}

}
