#!/bin/bash 
#SBATCH --partition=dggpu 
#SBATCH --nodes=1 
#SBATCH --ntasks-per-node=8 
#SBATCH --gres=gpu:1 
#SBATCH --mem=32G 
#SBATCH --time=2:00:00 
#SBATCH --mail-type=ALL 
#SBATCH --job-name=job_k