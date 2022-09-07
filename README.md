## ITE2038 : DATABASE SYSTEMS
> B+ Tree

B+ Tree의 주요 기능인 `Insertion`, `Deletion`, `Single Key Search`, `Ranged Search`를 지원합니다.<br>
코드에 대한 자세한 설명은 `description.pdf`에 있습니다.<br>
<br>

제가 구현한 `insertion`에서 따르는 규칙은 다음과 같습니다.<br>
* 삽입 후 overflow가 발생하지 않으면 종료합니다.
* 삽입 후 overflow가 발생하면 다음 조건을 따라 수행됩니다.
  - left sibling이 존재할 때, left sibling의 key pair 개수가 `b-1`미만이면 현재 노드에서 가장 작은 key pair를 left sibling으로 넘깁니다.
  - right sibling이 존재할 때, right sibling의 ker pair 개수가 `b-1`미만이면 현재 노드에서 가장 작은 key pair를 right sibling으로 넘깁니다.
  - split 후 중간 key pair를 부모 노드에 삽입합니다.


제가 구현한 Deletion에서 따르는 규칙은 다음과 같습니다.<br>
* 삭제 후 underflow가 발생하지 않으면 종료합니다.
* 삭제 후 underflow가 발생하면 다음 조건을 따라 수행됩니다.
  - left sibling의 key pair 개수가 `[b]-1`보다 많으면 하나를 빌려옵니다.
  - right sibling의 key pair 개수가 `[b]-1`보다 많으면 하나를 빌려옵니다.
  - (left sibling이 존재한다면) left sibling으로 merge합니다.
  - (right sibling이 존재한다면) right sibling으로 merge합니다.

<br>

> 프로젝트 4 : DBMS 프로그램

다음 relation schema를 기반으로 하는 DB를 연동한 프로그램입니다.<br>
<div style="text-align : center;">
<img src="https://drive.google.com/uc?id=1FknL21O41HXMqmgUum6p9Hp_ZAz_1ST8" width="50%" height="50%" title="project4_relation_schema.png" alt="?"/>
</div>

입력받은 정보를 SQL문에 넣고 DB에 쿼리를 날려 필요에 따라 정보를 가져오거나 업데이트하며 동작합니다.<br>
- [more details...](https://github.com/yooniversal/ITE2038/blob/main/Project4/2018062733_%EC%9C%A4%EB%8F%99%EB%B9%88_P4.pdf)
