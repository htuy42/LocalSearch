package com.sbf.ls

import java.io.Serializable
import java.util.*

typealias Proposition = (Assignment) -> Assignment
typealias Objective = (Assignment) -> Double

val QUEUE_SIZE = 100

class LSBox : Serializable{
    val bests : ArrayList<ScoredAssignment> = ArrayList()

    val valids = ArrayList<ScoredAssignment>()
    val all = ArrayList<ScoredAssignment>()
    val rand = Random()

    fun getNext() : Assignment{
        val r = rand.nextDouble()
        if(r < .3){
            return bests[rand.nextInt(bests.size)].assignment
        } else if(r < .6)  {
            return all[rand.nextInt(all.size)].assignment
        } else {
            return valids[rand.nextInt(valids.size)].assignment
        }
    }

    fun store(toStore : ScoredAssignment){
        all.add(toStore)
        if(toStore.score < Double.MAX_VALUE){
            valids.add(toStore)
        }
        if(bests.size > 5){
            val worstInd =(0 until bests.size).maxBy { bests[it].score }!!
            bests[worstInd] = toStore
        } else {
            bests.add(toStore)
        }
    }
}