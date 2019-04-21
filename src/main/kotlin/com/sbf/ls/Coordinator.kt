package com.sbf.ls

import com.htuy.common.Address
import com.htuy.netlib.sockets.Socket
import com.htuy.swarm.management.Service
import java.util.*

val maxElites = 100

val COORDINATOR_SERVICE_TYPE = "coordinator"
val WORKER_SERVICE_TYPE = "worker"

class Coordinator : Service {
    override var done: Boolean = false
    override lateinit var socketAddress: Address
    lateinit var objective : Objective
    var elites : PriorityQueue<ScoredAssignment> = PriorityQueue(object : Comparator<ScoredAssignment>{
        override fun compare(o1: ScoredAssignment, o2: ScoredAssignment): Int {
            return o2.score.compareTo(o1.score)
        }
    })
    lateinit var propositions : List<Proposition>

    override fun passArgs(args: Array<String>) {
        val instance = VRPInstance(args[0])
        objective = instance.getObjective()
        propositions = instance.getPropositions()
        val initial = instance.getInitial()
        elites.add(ScoredAssignment(initial,objective(initial)))
    }



    override fun register(socket: Socket) {
        socket.registerTypeListener(WorkRequest::class.java){
            synchronized(this) {
                WorkResponse(elites.toList(), propositions, objective)
            }
        }
        socket.registerTypeListener(Result::class.java){
            synchronized(this) {
                elites.addAll(it.elites)
                while (elites.size > maxElites) {
                    elites.poll()
                }
            }
            null
        }
    }
}