package test.utils

import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.test.FakeRequest
import play.api.http.HeaderNames

object FakeJsonRequest {

  def apply(method: String, uri: String, headers: Map[String, String], json: JsValue): FakeRequest[JsValue] = {
    val jsonHeaders = Map(HeaderNames.CONTENT_TYPE -> "application/json") ++ headers

    FakeRequest(method, uri).
    withHeaders(jsonHeaders.toSeq: _*).
    copy(body = json)
  }

  def apply(method: String, uri: String, headers: Map[String, String], jsonString: String): FakeRequest[JsValue] = {
    apply(method, uri, headers, Json.parse(jsonString))
  }

  def apply(method: String, uri: String, json: JsValue): FakeRequest[JsValue] = {
    apply(method, uri, Map[String, String](), json)
  }

  def apply(method: String, uri: String, jsonString: String): FakeRequest[JsValue] = {
    apply(method, uri, Map[String, String](), jsonString)
  }
}