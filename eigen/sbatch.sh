#!/bin/bash
output_file="test.out"
error_file="test.err"
sbatch --job-name=test_job --output=${output_file} --error=${error_file} run.slurm