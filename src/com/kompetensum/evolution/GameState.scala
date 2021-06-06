package com.kompetensum.evolution

trait GameState {
	def init
	def update:Boolean
	def render
	def cleanup
}