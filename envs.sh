#!/bin/bash
# change the values for env variables here to set different
# parameter values for the simulator

# chooses the number of workers to use
export DISTML_NUM_WORKERS=1
# the network queuing strategy [FirstInFirstOut | SmallestIterationFirst]
export DISTML_QUEUE_TYPE="SmallestIterationFirst"
# base directory for data
export DISTML_DATA_DIR=${PWD}/data
# data set to use
export DISTML_DATASET="criteo"

# type of delay seen at the worker [Uniform | Exponential | Pareto]
export DISTML_DELAY_TYPE="Pareto"

# parameters of uniform distribution
export DISTML_DELAY_UNIFORM_LOW=0
export DISTML_DELAY_UNIFORM_HIGH=4

# parameters of exponential distribution
export DISTML_DELAY_EXP_LAMBDA=0.5

# parameters of pareto distribution
export DISTML_DELAY_PARETO_XM=1.0
export DISTML_DELAY_PARETO_ALPHA=0.5

# SGD paramters
export SGD_MINI_BATCH_SIZE=500
export SGD_MAX_ITERATIONS=50
export SGD_LEARNING_RATE=0.01
export SGD_TIME_LIMIT=5
