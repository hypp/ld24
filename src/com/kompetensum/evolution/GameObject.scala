package com.kompetensum.evolution

class GameObject {
  
  var x = 0.0
  var y = 0.0
  var z = 0.0
  
  var boundingCircleRadius = 0.0
  
  var speed = 0.00
  
  def getCenter = {
    (x, y, z)
  }
  
  def setCenter(xpos:Double,ypos:Double,zpos:Double) {
    x = xpos
    y = ypos
    z = zpos
  }
  
  def update {
  }
  
  def render {
  }
  
  def checkCollision(obj:GameObject) = {
    false
  }
  
  def getBoundingCircle = {
    (x,y,boundingCircleRadius)
  }
  
  def distance(x1:Double,y1:Double,x2:Double,y2:Double) = {
    val dx = x1 - x2
    val dy = y1 - y2
    Math.sqrt(dx * dx + dy * dy)    
  }
  
  def pointInCircle(centerX:Double,centerY:Double,radius:Double,x2:Double,y2:Double) = {
    var dist = distance(centerX,centerY,x2,y2)
    dist <= radius
  }
  
  def circleWithCircle(centerX1:Double,centerY1:Double,radius1:Double,centerX2:Double,centerY2:Double,radius2:Double) = {
	  var dx = centerX1 - centerX2
	  var dy = centerY1 - centerY2
	  var dist = dx * dx + dy * dy 
	  var radii = radius1 + radius2
	  
	  dist <= (radii * radii)
  }

}