package com.sbf.ls


import java.awt.Point
import java.io.File
import java.io.FileNotFoundException
import java.lang.Math.sqrt

import java.util.Scanner

class VRPInstance(fileName: String) {
    // VRP Input Parameters
    internal var numCustomers: Int = 0                // the number of customers
    internal var numVehicles: Int = 0            // the number of vehicles
    internal var vehicleCapacity: Int = 0            // the capacity of the vehicles
    internal var demandOfCustomer: IntArray        // the demand of each customer
    internal var xCoordOfCustomer: DoubleArray    // the x coordinate of each customer
    internal var yCoordOfCustomer: DoubleArray    // the y coordinate of each customer

    init {
        var read: Scanner? = null
        try {
            read = Scanner(File(fileName))
        } catch (e: FileNotFoundException) {
            println("Error: in VRPInstance() " + fileName + "\n" + e.message)
            System.exit(-1)
        }

        numCustomers = read!!.nextInt()
        numVehicles = read.nextInt()
        vehicleCapacity = read.nextInt()

        println("Number of customers: $numCustomers")
        println("Number of vehicles: $numVehicles")
        println("Vehicle capacity: $vehicleCapacity")

        demandOfCustomer = IntArray(numCustomers)
        xCoordOfCustomer = DoubleArray(numCustomers)
        yCoordOfCustomer = DoubleArray(numCustomers)

        for (i in 0 until numCustomers) {
            demandOfCustomer[i] = read.nextInt()
            xCoordOfCustomer[i] = read.nextDouble()
            yCoordOfCustomer[i] = read.nextDouble()
        }

        for (i in 0 until numCustomers)
            println(demandOfCustomer[i].toString() + " " + xCoordOfCustomer[i] + " " + yCoordOfCustomer[i])
    }

    fun isValid(assignment: Assignment) : Boolean{
        // truck load
        for(elt in assignment.truckCustomers){
            var cost = 0.0
            for(sub in elt){
                cost += demandOfCustomer[sub]
            }
            if(cost > vehicleCapacity){
                return false
            }
        }
        // customers served once
        val served = HashSet<Int>()
        for(elt in assignment.truckCustomers){
            for(sub in elt){
                if(sub in served){
                    return false
                }
                served.add(sub)
            }
        }
        return served.size == numCustomers
    }

    fun getObjective() : Objective{
        return {
            if(!isValid(it)){
                Double.MAX_VALUE
            } else {
                var score = 0.0
                for (elt in it.truckCustomers) {
                    var xPos = 0.0
                    var yPos = 0.0
                    for (destination in elt) {
                        val nextX = xCoordOfCustomer[destination]
                        val nextY = yCoordOfCustomer[destination]
                        val xD = nextX - xPos
                        val yD = nextY - yPos
                        score += sqrt((xD * xD) + (yD * yD))
                        xPos = nextX
                        yPos = nextY
                    }
                    // we need to go home
                    val nextX = 0.0
                    val nextY = 0.0
                    val xD = nextX - xPos
                    val yD = nextY - yPos
                    score += sqrt((xD * xD) + (yD * yD))
                }
                score
            }
        }
    }

    fun getPropositions() : List<Proposition>{
        return listOf()
    }

    fun getInitial() : Assignment{
        val lst : MutableList<MutableList<Int>> = arrayListOf()
        var currentIndex = 0
        var currentSubLst = ArrayList<Int>()
        var currentSubLoad = 0
        while(currentIndex < numCustomers){
            val cost = demandOfCustomer[currentIndex]
            assert(cost <= vehicleCapacity)
            if(cost + currentSubLoad > vehicleCapacity){
                lst.add(currentSubLst)
                currentSubLoad = 0
                currentSubLst = arrayListOf()
            } else {
                currentSubLoad += cost
                currentSubLst.add(currentIndex)
                currentIndex++
            }
        }
        while(lst.size < numVehicles){
            lst.add(arrayListOf())
        }
        return Assignment(lst)
    }
}

