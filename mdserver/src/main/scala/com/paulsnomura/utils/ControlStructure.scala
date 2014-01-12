package com.paulsnomura.utils

object ControlStructure {
    def safeResourceFunction( resourceCodeToTry : => Unit )( resourceCleanup : => Unit )( finalCleanup : => Unit ) = {
        try{
            resourceCodeToTry
        }
        catch {
            case e: Exception =>{
            	try{
            		resourceCleanup
            	}
            	finally{
            	    finalCleanup
            	    throw e //Do not swallow the exception
            	}            	           
            }
        }
    }
}