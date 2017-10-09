#!/bin/bash

echo "Applying migration TellUsMore"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /tellUsMore                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.TellUsMoreController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /tellUsMore                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.TellUsMoreController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTellUsMore                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.TellUsMoreController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTellUsMore                       uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.TellUsMoreController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "tellUsMore.title = tellUsMore" >> ../conf/messages.en
echo "tellUsMore.heading = tellUsMore" >> ../conf/messages.en
echo "tellUsMore.field1 = Field 1" >> ../conf/messages.en
echo "tellUsMore.field2 = Field 2" >> ../conf/messages.en
echo "tellUsMore.checkYourAnswersLabel = tellUsMore" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def tellUsMore: Option[TellUsMore] = cacheMap.getEntry[TellUsMore](TellUsMoreId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def tellUsMore: Option[AnswerRow] = userAnswers.tellUsMore map {";\
     print "    x => AnswerRow(\"tellUsMore.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.TellUsMoreController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration TellUsMore completed"
