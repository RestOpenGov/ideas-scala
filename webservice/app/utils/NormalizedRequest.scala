package utils

import play.api.mvc.RequestHeader

import play.api.Play.current

class NormalizedRequest(request: RequestHeader) extends RequestHeader {

  val headers = request.headers
  val queryString = request.queryString
  val remoteAddress = request.remoteAddress
  val method = request.method

  // strip first part of path and uri if it matches http.path config
  val path = request.path.stripSuffix("/")
  val uri = path + {
    if(request.rawQueryString == "") ""
    else "?" + request.rawQueryString
  }
}

object NormalizedRequest {
  def apply(request: RequestHeader) = new NormalizedRequest(request)
}