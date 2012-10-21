package controllers

import models.IdeaType

import formatters.json.IdeaTypeFormatter._

object CrudIdeaTypes extends CrudController[IdeaType](
  model = IdeaType, 
  name = "type of idea", 
  formatter = JsonIdeaTypeFormatter
)