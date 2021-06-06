package com.kompetensum.evolution

import java.awt.Font
import org.lwjgl._
import opengl.{Display,GL11,DisplayMode}
import GL11._
import input._
import Keyboard._
//import scala.util._
import org.newdawn.slick.Color
import org.newdawn.slick.opengl.Texture
import org.newdawn.slick.opengl.TextureLoader
import org.newdawn.slick.util.ResourceLoader
import scala.util._

class IntroGameState(audioManager:AudioManager) extends GameState {

  val audio = audioManager
  
  val sampleLetterName = "data/Blip_Select7.wav"
  val sampleLetter = audio.loadALSample(sampleLetterName, false)

  var texture:Texture = null
  
  val message = "Evolution\nYou are a Triangle\nYou want to evolve\ninto the divine\nform Circle\n\nEat the green\n\nGame controls: i j k l\nPress space to begin"
  var currentChar = -1
  val startX = -13f
  val startY = 9f
  val xSpacing = 1.1f
  val ySpacing = 1.6f
  var currentX = startX
  var currentY = startY
  val timeBetweenChars = 10
  var countDown = timeBetweenChars
  
  var letters:List[(Char,Float,Float,Float)] = List.empty
  
  def init(): Unit = {
    if (texture == null) {
      texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("data/SimHei.png"))
    }
    
    currentChar = -1
    currentX = startX
    currentY = startY
    countDown = timeBetweenChars
    letters = List.empty
  }

  def update(): Boolean = {
    
    if (isKeyDown(KEY_SPACE)) {
      return true
    }

    if (countDown == 0) {
    	nextLetter
    	countDown = timeBetweenChars
    } else {
    	countDown -= 1
    }
    
    false
  }
  
  def nextLetter {
    currentChar += 1
    currentX += xSpacing
    
    if (currentChar >= message.length) {
      // Done!
      currentChar = message.length
      return
    }
    
    if (message(currentChar) == '\n') {
      currentY -= ySpacing
      currentX = startX
      return
    }
    
    if (message(currentChar) == ' ') {
      return
    }
    
    val xnoise = Random.nextFloat * 0.2f
    val ynoise = Random.nextFloat * 0.5f
    
    letters = (message(currentChar),currentX + xnoise,currentY + ynoise,-5f) :: letters
    audio.play(sampleLetter)
  }
  
  def drawLetter(letter:Int,x:Float,y:Float,z:Float) {
	  // Figure out texture coords for A
	  // 16 columns x 16 rows
	  val colWidth = 1f / 16f
	  val colHeight = 1f / 16f
	  
	  val rowForA = letter / 16
	  val colForA = letter % 16
	  val xLeftA = colForA * colWidth
	  val xRightA = xLeftA + colWidth
	  val yTopA = rowForA * colHeight
	  val yBottomA = yTopA + colHeight

	  glPushMatrix
	  glTranslatef(x,y,z)

	  glBegin(GL11.GL_QUADS)
	  glTexCoord2f(xLeftA,yTopA);
	  glVertex3f(-1,1,-5);
	  glTexCoord2f(xRightA,yTopA);
	  glVertex3f(1,1,-5);
	  glTexCoord2f(xRightA,yBottomA);
	  glVertex3f(1,-1,-5);
	  glTexCoord2f(xLeftA,yBottomA);
	  glVertex3f(-1,-1,-5);
	  glEnd();	  

	  glPopMatrix

  }

  def render(): Unit = {
    
	  glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
	  glLoadIdentity
	   
//	  glTranslatef(0,0,-20)

	  glEnable(GL11.GL_TEXTURE_2D)
	  glEnable(GL11.GL_BLEND);
	  glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	  glAlphaFunc(GL_GREATER, 0.5f);
	  glEnable(GL_ALPHA_TEST);
	  
	  Color.white.bind()
	  texture.bind(); // or GL11.glBind(texture.getTextureID());

	  letters.foreach(l => {
	    val (letter, x, y, z) = l
	    drawLetter(letter,x,y,z)
	  })

  }

  def cleanup(): Unit = {
	  glDisable(GL11.GL_TEXTURE_2D)
	  glDisable(GL11.GL_BLEND);
	  glDisable(GL_ALPHA_TEST);
  }

}