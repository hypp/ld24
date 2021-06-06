package com.kompetensum.evolution

import org.lwjgl._
import opengl.{GL11}
import GL11._
import Math._
import scala.util._

class GoodFood extends Food {
    
  var numPoints = 2
  
  var rotationSpeed = -20
  var rotation = 0.0
  
  boundingCircleRadius = radius
  
  val STARTING = 0
  val MOVING = 1
  val DONE = 2
  var state = STARTING
  
  def closeTo(obj:Player) {
    val (ox, oy, oradius) = obj.getBoundingCircle
    
	val dx = ox - x
	val dy = oy - y
	val dist = dx * dx + dy * dy 
	val radii = oradius + radius
	val radii2 = 2 * radii
	val radii4 = 4 * radii

	if (dist <= radii * radii) {
	  speed = flightSpeed
	} else if (dist <= radii2 * radii2) {
	  speed = flightSpeed
	  
	  if (ox < x) {
	    targetX = x + radii2
	  } else {
	    targetX = x - radii2
	  }
	  
	  if (oy < y) {
	    targetY = y + radii2
	  } else {
	    targetY = y - radii2
	  }
	  
	} else if (dist <= radii4 * radii4) {
      // Change direction 
      targetX = -targetY
      targetY = -targetX	  
	} else {
	  // Far away
	  speed = defaultSpeed
	}    
  }
  
  override def update {
    rotation += rotationSpeed
    if (rotation > 360) {
      rotation -= 360
    }
    if (rotation < 0) {
      rotation += 360
    }
    
    if (state == STARTING) {
    	targetX = Random.nextDouble * 20 - 10
    	targetY = Random.nextDouble * 20 - 10
    	speed = defaultSpeed
    	state = MOVING
    }
    
    if (state == MOVING) {
      val dx = x - targetX
      val dy = y - targetY
      val speedx = speed * 0.6 + speed * 0.4 * Math.sin(dy)
      val speedy = speed * 0.6 + speed * 0.4 * Math.sin(dx)
      if (dx < 0) {
    	  x += speedx;
      } else {
    	  x -= speedx;        
      }
      
      if (dy < 0) {
    	  y += speedy;
      } else {
    	  y -= speedy;
      }
      
      if (Math.abs(dx) < 0.1 && Math.abs(dy) < 0.1) {
        state = DONE
      }

      if (x < -10) {
        x = -10
        state = DONE
      }
      if (x > 10) {
        x = 10
        state = DONE
      }

      if (y < -10) {
        y = -10
        state = DONE
      }
      if (y > 10) {
        y = 10
        state = DONE
      }

    }
    
    if (state == DONE) {
      state = STARTING
    }
    
  }
  
  override def render {
    glPushMatrix
    
	glColor3f(0.5f,1,0)
	glDisable(GL_LIGHTING)
	
	glTranslatef(x.toFloat,y.toFloat,-5)
	
    glRotatef(rotation.toFloat,0,0,1)

    glBegin(GL_LINE_LOOP)
    
    val increment = 2 * Math.Pi / numPoints
    for (i <- 1 to numPoints) {
      val angle = i * increment;
      val xcoord = Math.sin(angle) * radius
      val ycoord = Math.cos(angle) * radius
      val zcoord = -5.0

      glVertex3f(xcoord.toFloat,ycoord.toFloat,zcoord.toFloat)
    }
    
    glEnd
    
    glEnable(GL_LIGHTING)
    
    glPopMatrix
  }
  
  override def mate(obj:Food):Food = {
    var f : Food = null
    if (matingState == M_NEWBORN) {
      return f
    }
    if (matingState != M_DONE) {
      matingState = M_DONE
      obj match {
        case y: EvilFood => {
		      if (Random.nextBoolean) {
		    	  f = new EvilFood()
		      } else {
		    	  f = new GoodFood()
		      }
        }
        case y: GoodFood => f = new GoodFood()
        case _ => f = new GoodFood()
      }      
    }
    f
  }
  
}