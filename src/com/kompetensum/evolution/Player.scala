package com.kompetensum.evolution

import org.lwjgl._
import opengl.{GL11}
import GL11._
import Math._

class Player extends GameObject {  

  var radius = 0.4

  var numPoints = 3
  
  var rotationSpeed = 1
  var rotation = 0.0

  boundingCircleRadius = radius
  speed = 0.06

  def getPoints = {
    numPoints
  }
  
  def addPoint {
    numPoints += 1
    radius += 0.1
    boundingCircleRadius = radius
    rotationSpeed = (1.0 - numPoints * 0.5).toInt
  }
  
  def removePoint {
    numPoints -= 1
    radius -= 0.1
    boundingCircleRadius = radius
    rotationSpeed = Math.max(1.0 - numPoints * 0.5,1).toInt
  }

  def move(left:Boolean,right:Boolean,up:Boolean,down:Boolean) {
	  var x1 = 0
	  var x2 = 0
	  var y1 = 0
	  var y2 = 0
	  
	  if (left) {
	    x1 = -1
	  }
	  if (right) {
	    x1 = 1
	  }
	  if (up) {
	    y1 = 1
	  }
	  if (down) {
	    y1 = -1
	  }
	  
	  if (x1 == 0 && y1 == 0) {
	    return
	  }
	  
	  val dist = distance(x1,y1,x2,y2)
	  val normalizedSpeed = dist / speed
	   
	  x += x1 / normalizedSpeed
	  y += y1 / normalizedSpeed
  }
  
  override def checkCollision(obj:GameObject) = {
    var (x1,y1,radius1) = obj.getBoundingCircle
    if (circleWithCircle(x,y,radius,x1,y1,radius1)) {
    	// TODO Do more tests
    	true
    } else {
    	false
    }
  }
  
  override def update {
    rotation += rotationSpeed
    if (rotation > 360) {
      rotation -= 360
    }
  }
  
  override def render {
    
	glDisable(GL_LIGHTING)
	
	glTranslatef(x.toFloat,y.toFloat,-5)
	
    glRotatef(rotation.toFloat,0,0,1)

    val increment = 2 * Math.Pi / numPoints

//    glDisable(GL_DEPTH_TEST)
    glBegin(GL_LINE_LOOP)
    
    for (i <- 1 to numPoints) {
      if (i % 2 == 0) {
        glColor3f(1,0,0)
      } else {
        glColor3f(0,1,0)        
      }
      val angle = i * increment;
      val xcoord = Math.sin(angle) * radius
      val ycoord = Math.cos(angle) * radius
      val zcoord = -5.0

      glVertex3f(xcoord.toFloat,ycoord.toFloat,zcoord.toFloat)
    }

    glEnd
    
//    glEnable(GL_DEPTH_TEST)
    glEnable(GL_LIGHTING)
  }
  
}