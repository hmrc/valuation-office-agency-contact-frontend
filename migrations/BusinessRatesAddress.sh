#!/bin/bash

echo "Applying migration BusinessRatesAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /businessRatesAddress                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.BusinessRatesAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /businessRatesAddress                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.BusinessRatesAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBusinessRatesAddress                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.BusinessRatesAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBusinessRatesAddress                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.BusinessRatesAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "businessRatesAddress.title = businessRatesAddress" >> ../conf/messages.en
echo "businessRatesAddress.heading = businessRatesAddress" >> ../conf/messages.en
echo "businessRatesAddress.field1 = Field 1" >> ../conf/messages.en
echo "businessRatesAddress.field2 = Field 2" >> ../conf/messages.en
echo "businessRatesAddress.checkYourAnswersLabel = businessRatesAddress" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def businessRatesAddress: Option[BusinessRatesAddress] = cacheMap.getEntry[BusinessRatesAddress](BusinessRatesAddressId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def businessRatesAddress: Option[AnswerRow] = userAnswers.businessRatesAddress map {";\
     print "    x => AnswerRow(\"businessRatesAddress.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.BusinessRatesAddressController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BusinessRatesAddress completed"
