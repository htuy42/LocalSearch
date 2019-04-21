package com.sbf.ls

import java.io.Serializable

class WorkRequest : Serializable

data class WorkResponse(val elites : List<ScoredAssignment>, val propositions : List<Proposition>, val objective : Objective) : Serializable

data class Result(val elites : List<ScoredAssignment>) : Serializable