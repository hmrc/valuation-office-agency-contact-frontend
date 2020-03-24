/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.valuationofficeagencycontactfrontend.utils

import uk.gov.hmrc.valuationofficeagencycontactfrontend.SpecBase
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{ConfirmedContactDetails, ContactDetails}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.ContactFormatter._

class ContactFormatterSpec extends SpecBase {

  "Contact Formatter" must {

    "Given a complete Contact Details it should generate a formatted string using the given interstitial" in {
      val cd = ContactDetails("a", "b", "c", "d", "e")
      formattedContactDetails(Some(cd), "<br/>") mustBe "a b<br/>c<br/>e"
    }

    "Given a Contact Details with elements that have too many spaces it should generate a formatted string using the given interstitial" in {
      val cd = ContactDetails(" a ", " b ", " c ", " d ", " e ")
      formattedContactDetails(Some(cd), "<br/>") mustBe "a b<br/>c<br/>e"
    }

    "Given no Contact Details it should generate am empty string" in {
      formattedContactDetails(None, "<br/>") mustBe ""
    }

    "Given a complete Confirmed Contact Details it should generate a formatted string using the given interstitial" in {
      val ccd = ConfirmedContactDetails("a", "b", "c", "e")
      formattedConfirmedContactDetails(ccd, "<br/>") mustBe "a b<br/>c<br/>e"
    }

    "Given a Confirmed Contact Details with elements that have too many spaces it should generate a formatted string using the given interstitial" in {
      val ccd = ConfirmedContactDetails(" a ", " b ", " c ", " e ")
      formattedConfirmedContactDetails(ccd, "<br/>") mustBe "a b<br/>c<br/>e"
    }

    "Given a Sequence with three strings insert the interstitials" in {
      insertInterstitials(Seq("a", "b", "c"), ",") mustBe "a,b,c"
    }

    "Given an empty sequence return an empty String" in {
      insertInterstitials(Seq(), ",") mustBe ""

    }

    "Given a sequence of strings that have leading or trailing spaces return the formatted string without the leading or trailing spaces" in {
      insertInterstitials(Seq(" a ", " b ", " c "), ",") mustBe "a,b,c"
    }

  }
}

