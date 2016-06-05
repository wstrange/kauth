package io.kauth

import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * Created by warren on 2016-06-04.
 * from http://stackoverflow.com/questions/34416869/idiomatic-way-of-logging-in-kotlin
 */


// Return logger for Java class, if companion object fix the name
public fun <T: Any> logger(forClass: Class<T>): Logger {
    return Logger.getLogger(unwrapCompanionClass(forClass).name)
}

// unwrap companion class to enclosing class given a Java Class
public fun <T: Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    // WS : not working and i dont understand why
//    return if (ofClass.enclosingClass != null &&
//            //ofClass.enclosingClass.kotlin.companionObject?.java
//            ofClass.enclosingClass.kotlin.
//                    == ofClass) {
//        ofClass.enclosingClass
//    } else {
//        ofClass
//    }
    return ofClass
}

// unwrap companion class to enclosing class given a Kotlin Class
public fun <T: Any> unwrapCompanionClass(ofClass: KClass<T>): KClass<*> {
    return unwrapCompanionClass(ofClass.java).kotlin
}

// Return logger for Kotlin class
public fun <T: Any> logger(forClass: KClass<T>): Logger {
    return logger(forClass.java)
}

// return logger from extended class (or the enclosing class)
public fun <T: Any> T.logger(): Logger {
    return logger(this.javaClass)
}

// return a lazy logger property delegate for enclosing class
public fun <R : Any> R.lazyLogger(): Lazy<Logger> {
    return lazy { logger(this.javaClass) }
}

// return a logger property delegate for enclosing class
public fun <R : Any> R.injectLogger(): Lazy<Logger> {
    return lazyOf(logger(this.javaClass))
}

// marker interface and related extension (remove extension for Any.logger() in favour of this)
interface Loggable {}
public fun Loggable.logger(): Logger = logger(this.javaClass)

// abstract base class to provide logging, intended for companion objects more than classes but works for either
public abstract class WithLogging: Loggable {
    val LOG = logger()
}
