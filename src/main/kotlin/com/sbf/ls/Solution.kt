package com.sbf.ls

import java.io.Serializable

class Assignment(
    val truckCustomers : MutableList<MutableList<Int>> = arrayListOf()
) : Serializable

class ScoredAssignment(val assignment : Assignment, val score : Double) : Serializable