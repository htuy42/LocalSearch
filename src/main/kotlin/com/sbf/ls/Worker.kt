package com.sbf.ls

import com.htuy.common.Address
import com.htuy.netlib.sockets.Socket
import com.htuy.swarm.management.King
import com.htuy.swarm.management.Service
import kotlinx.coroutines.experimental.runBlocking
import java.util.*

val TIME_TO_WORK_FOR = 5000

class Worker(val king : King) : Service{
    override var done: Boolean = false
    override lateinit var socketAddress: Address
    var box = LSBox()
    val random = Random()
    lateinit var currentReuquest : WorkResponse

    override fun startService() = runBlocking{
        currentReuquest = (king.tryGetSynchToServiceRetry(COORDINATOR_SERVICE_TYPE,WorkRequest()) as WorkResponse)

        work()
    }

    fun work()= runBlocking{
        /// do work for x seconds
        for(elt in currentReuquest.elites){
            box.store(elt)
        }
        val start = System.currentTimeMillis()
        while(System.currentTimeMillis() - start < TIME_TO_WORK_FOR){
            for(i in 0..100){
                val next = box.getNext()
                val ind = random.nextInt(currentReuquest.propositions.size)
                val proposed = currentReuquest.propositions[ind].invoke(next)
                val scored = ScoredAssignment(proposed,currentReuquest.objective(proposed))
                box.store(scored)
            }
        }


        king.tryPushToServiceRetry(COORDINATOR_SERVICE_TYPE,Result(box.bests.toList()))
        currentReuquest = king.tryGetSynchToServiceRetry(COORDINATOR_SERVICE_TYPE,WorkRequest()) as WorkResponse
        box = LSBox()
        work()
    }

    override fun register(socket: Socket) {
        // nothing to register
    }
}