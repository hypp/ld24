package com.kompetensum.evolution

class Food extends GameObject {
  val defaultSpeed = 0.04
  val flightSpeed = 0.08
  val radius = 0.2
  
  var targetX = 0.0
  var targetY = 0.0
  
  var t = 0

  val M_MEET = 0
  val M_AWAY = 1
  val M_DONE = 2
  val M_NEWBORN = 3
  
  var matingState = M_NEWBORN 

  def closeTo(obj:Food) {
    if (obj == this) {
      return
    }
    val (ox, oy, oradius) = obj.getBoundingCircle
    
	val dx = ox - x
	val dy = oy - y
	val dist = dx * dx + dy * dy 
	val radii = oradius + radius
	val radii2 = 2 * radii
	val radii4 = 4 * radii

	t += 1
	
	if (dist <= radii * radii) {
	  if (matingState == M_MEET) {
		  targetX = -ox
		  targetY = -oy
		  
		  speed = flightSpeed
		  matingState = M_AWAY
	  }
	} else if (dist <= radii2 * radii2) {
	  targetX = targetX - radii2 * Math.cos(t)
	  targetY = targetY - radii2 * Math.sin(t)

	  speed = defaultSpeed	  	  
	} else if (dist <= radii4 * radii4) {
	  if (matingState == M_MEET) {
	      // Change direction 
		  targetX = ox - radii * Math.cos(t)
		  targetY = oy - radii * Math.sin(t)
		  speed = flightSpeed
	  }
	  if (matingState == M_AWAY) {
	    matingState == M_MEET
	  }
	} else {
	  // Far away
	  speed = defaultSpeed
		if (matingState == M_AWAY) {
		  matingState = M_MEET
		}
	  
	  if (t > 60 * 10 && matingState == M_NEWBORN) {
	    matingState = M_MEET
	  }
	}
  }

  override def checkCollision(obj:GameObject) = {
    if (obj == this) {
      false
    } else {
	    var (x1,y1,radius1) = obj.getBoundingCircle
	    if (circleWithCircle(x,y,radius,x1,y1,radius1)) {
	    	// TODO Do more tests
	    	true
	    } else {
	    	false
	    }
    }
  }
  
	def mate(obj:Food):Food = {
	    var f : Food = null
	    f
	}

}