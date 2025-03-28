#!/bin/bash

# SERVER_TYPE="be"
# S3_BUCKET="your-test-bucket"

PATCH_VERSIONS=$(aws s3 ls| while read -r line; do
  FILE=$(echo "$line" | xargs | cut -d' ' -f4)

  if [[ "$FILE" == ${SERVER_TYPE}-*.zip ]]; then
    # 예: fe-1.0.13.zip → 13 추출
    VERSION_PART=${FILE%.zip}              # .zip 제거 → fe-1.0.13
    VERSION_ONLY=${VERSION_PART#${SERVER_TYPE}-} # fe- 제거 → 1.0.13
    PATCH=${VERSION_ONLY##*.}              # 마지막 . 기준 → 13
    echo "$PATCH"
  fi
done)

LATEST_PATCH=$(echo "$PATCH_VERSIONS" | sort -n | tail -n 1)

echo "최신 patch 버전: $LATEST_PATCH"

NEXT_PATCH=$((LATEST_PATCH + 1))
NEW_VERSION="1.0.${NEXT_PATCH}"
NEW_FILENAME="${SERVER_TYPE}-${NEW_VERSION}.zip"

OLD_FILENAME="${SERVER_TYPE}-1.0.${LATEST_PATCH}.zip"
unzip $OLD_FILENAME -d ./extracted

 sed -i "s/backend-repo:.*/backend-repo:${NEW_VERSION}/" ./extracted/deploy.sh

cd extracted
zip -r ./$NEW_FILENAME ./*
cd ..

# S3 업로드 (버전별 + latest.zip)
aws s3 cp $NEW_FILENAME s3://${S3_BUCKET}/${SERVER_TYPE}/
cp $NEW_FILENAME latest.zip
aws s3 cp latest.zip s3://${S3_BUCKET}${SERVER_TYPE}/

# Github Actions용 환경변수 출력
echo "NEW_VERSION=${NEW_VERSION}" >> $GITHUB_ENV
echo "NEW_FILENAME=${NEW_FILENAME}" >> $GITHUB_ENV