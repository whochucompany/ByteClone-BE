package com.whochucompany.byteclone.repository;

import com.whochucompany.byteclone.domain.news.News;
import com.whochucompany.byteclone.domain.news.enums.Category;
import com.whochucompany.byteclone.domain.repository.NewsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static com.whochucompany.byteclone.domain.news.enums.View.GUEST;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class NewsRepositoryTest {

    @Autowired
    NewsRepository newsRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createNewsTest() {
        News news = News.builder()
                .title("국내 유가, 왜 7주째 하락하고 있을까?")
                .content("최근 유가가 7주 연속으로 하락세입니다. 최근 국제 정세와 각 국가의 경기 침체가 유가 하락의 원인으로 지목됐는데요. 7주 동안 가격이 하락했음에도 20일, 휘발유 1754.6원, 경유 1853.7원으로 소비자에게는 여전히 큰 부담입니다.\n" +
                        "\n" +
                        "유가 왜 이렇게 올랐을까?\n" +
                        "지난 3월, 국제 유가는 사상 최고치인 배럴당 147달러까지 치솟았습니다. 코로나19 이후의 국제 정세와 경제 회복이 배경이었는데요.\n" +
                        "\n" +
                        "코로나19 유행이 잦아든 이후 세계 각국의 경제가 회복되며 항공 업계 등의 석유 사용량이 증가했습니다.\n" +
                        "유럽과 미국의 탄소 배출 규제는 화석 연료 사용 시 발생하는 탄소에 세금을 부과합니다. 기업은 탄소세를 줄이기 위해 석유 대신 상대적으로 탄소 배출이 적은 천연가스를 사용했는데요.\n" +
                        "수요가 높아지자 천연가스의 가격이 상승해 탄소세를 더 많이 부과받더라도 석유를 사용하는 것이 더 이득이라고 판단한 기업들이 다시 석유로 눈을 돌렸습니다.\n" +
                        "러시아-우크라이나 전쟁 이후 국제사회는 러시아를 견제하기 위해 경제적 제재를 진행 중입니다. EU는 러시아 석탄 수입을 금지했고, 독일 등은 러시아의 석유 수입을 금지했습니다. 이런 제재가 유가 상승에 불을 붙였죠.\n" +
                        "최근에 유가가 다시 떨어지는 이유, 수요 감소\n" +
                        "그러나 최근에는 유가가 다소 하락세인데요. 코로나19 이후 과도한 인플레이션을 우려해 현재 세계적으로 금리를 인상하는 추세고, 중국의 제로 코로나 정책으로 인해 수요가 감소했기 때문입니다.\n" +
                        "\n" +
                        "금리가 오르면 이자 부담이 증가해 기업의 투자 활동이 감소합니다. 최근 연이은 미국 연준의 금리 인상은 투자 활동을 동결시키기 충분했죠.\n" +
                        "미국의 6월 물가 상승률은 40년 만에 9.1%까지 치솟았지만, 국내총생산(GDP) 성장률은 2개 분기 연속 마이너스를 기록했습니다. 기술적 경기 침체에 빠진 상황이라고도 말할 수 있죠.\n" +
                        "중국은 '제로 코로나' 정책을 펼치고 있는데요. 이는 산업 생산, 소비자 지출, 서비스 생산지수 등의 경기 지표가 악화하는 결과를 초래했습니다.\n" +
                        "최근에 유가가 다시 떨어지는 이유, 공급 증가\n" +
                        "석유의 공급이 다시 증가하고 있는 것도 원인이 됐습니다. 최근 고유가를 해결하기 위해 석유 증산과 이란 핵합의 등이 활발히 이루어지고 있는데요. 시장에 더 많은 석유가 공급되면 자연스럽게 가격이 내려가겠죠.\n" +
                        "\n" +
                        "최근 유럽연합(EU)은 미국에 이란 핵합의(JCPOA)와 관련된 중재 협상안을 제시했습니다. 이란의 핵합의가 복원되면 이란이 국제 원유 시장으로 복귀할 수 있을 것으로 기대됩니다.\n" +
                        "산유국들은 2021년 하반기부터 ‘점진적인 공급 정상화’를 통해 석유 시장 안정화를 위해 노력했습니다. 매월 하루 40만 배럴 증산이 목표였던 5,6월과 7,8월 계획을 각각 43.2만 배럴과 64.8만 배럴 증산으로 조정하며 유가 안정을 도모했죠.\n" +
                        "오는 9월에는 제31차 OPEC+ 회의가 열려 ‘하루 10만 배럴 증산’을 합의할 예정입니다. JMMC(OPEC+ 장관급 감시위원회)도 10만 배럴 증산을 권고하고 있기에 공급 증가에 대한 기대감에 불이 붙었습니다.\n" +
                        "향후 전망은?\n" +
                        "지난 18일 로이터 통신을 통해 OPEC 사무총장이 유가에 대한 입장을 표명했는데요.\n" +
                        "\n" +
                        "석유수출국기구(OPEC) 사무총장 하이탐 알가이스는 최근 국제유가 하락의 원인을 과도한 시장 우려로 꼽았습니다.\n" +
                        "이어 시장의 우려와 실물 경기가 다르다며 \"올해 하반기 세계 원유 수요는 여전히 매우 강할 것\"이라고 표명했는데요.\n" +
                        "국제에너지기구(IEA)는 올해 원유 수요를 9천 970만bpd(barrels per day; 배럴/일)로 전망했고 작년 대비 증가량 전망치는 210만bpd로 기존보다 38만bpd 늘려잡았습니다.\n" +
                        "OPEC과 러시아 등 비(非)OPEC 주요 산유국들의 협의체인 OPEC플러스(OPEC+) 정례 회의가 내달 5일로 예정된만큼, 향후 유가의 방향성을 알고 싶다면 정례 회의 내용에 집중해보면 어떨까요?")
                .image(null)
                .view(GUEST)
                .category(Category.DAILY_BYTE)
                .build();

        News savedNews = newsRepository.save(news);
    }

}