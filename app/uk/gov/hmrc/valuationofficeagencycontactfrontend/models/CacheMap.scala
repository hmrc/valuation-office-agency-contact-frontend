/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.valuationofficeagencycontactfrontend.models

import play.api.libs.json._

/**
  * Replacement for uk.gov.hmrc.http.cache.client.CacheMap
  *
  * @author Yuriy Tumakha
  */
case class CacheMap(id: String, data: Map[String, JsValue]) {

  def getEntry[T](key: String)(implicit fjs: Reads[T]): Option[T] =
    data
      .get(key)
      .map(json =>
        json
          .validate[T]
          .fold(
            errors => throw new KeyStoreEntryValidationException(key, json, CacheMap.getClass, errors),
            valid => valid
          )
      )
}

object CacheMap {
  implicit val formats: Format[CacheMap] = Json.format[CacheMap]
}

class KeyStoreEntryValidationException(
  val key: String,
  val invalidJson: JsValue,
  val readingAs: Class[?],
  val errors: scala.collection.Seq[(JsPath, scala.collection.Seq[JsonValidationError])]
) extends Exception {

  override def getMessage: String =
    s"KeyStore entry for key '$key' was '${Json.stringify(invalidJson)}'. Attempt to convert to ${readingAs.getName} gave errors: $errors"
}
