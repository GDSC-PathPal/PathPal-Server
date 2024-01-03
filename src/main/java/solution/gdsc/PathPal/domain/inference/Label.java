package solution.gdsc.PathPal.domain.inference;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Label {
    // 도보 상태
    flatness_A("평탄도 매우 좋음", "Flatness very good"),
    flatness_B("평탄도 좋음", "Flatness good"),
    flatness_C("평탄도 보통", "Flatness very good"),
    flatness_D("평탄도 안좋음", "Flatness very good"),
    flatness_E("평탄도 매우 안좋음", "Flatness very good"),
    walkway_paved("포장 도보", "Paved walkway"),
    walkway_block("블럭 도보", "Block walkway"),
    paved_state_broken("포장 도보 파손", "Paved walkway broken"),
    paved_state_normal("포장 도보 정상", "Paved walkway normal"),
    block_state_normal("블럭 도보 정상", "Block walkway normal"),
    block_kind_bad("블럭 도보 안좋음", "Block walkway bad"),
    block_kind_good("블럭 도보 좋음", "Block walkway good"),
    outcurb_rectangle("사각형 횡단보도", "Rectangle crosswalk"),
    outcurb_slide("슬라이드 횡단보도", "Slide crosswalk"),
    outcurb_rectengle_broken("사각형 횡단보도 파손", "Rectangle crosswalk broken"),

    // 도보 위험 요소
    sidegap_out("포장 도보 외부 사이드 갭", "Paved walkway outside side gap"),
    steepramp("경사로", "Steep ramp"),

    //특수 도보
    brailleblock_dot("점자 블럭", "Braille block"),
    brailleblock_line("점자 블럭", "Braille block"),
    brailleblock_dot_broken("점자 블럭 파손", "Braille block broken"),
    brailleblock_line_broken("점자 블럭 파손", "Braille block broken"),
    bicycleroad_broken("자전거 도로 파손", "Bicycle road broken"),
    bicycleroad_normal("자전거 도로 정상", "Bicycle road normal"),

    //횡단 보도
    planecrosswalk_broken("횡단보도 파손", "Plane crosswalk broken"),
    planecrosswalk_normal("횡단보도 정상", "Plane crosswalk normal"),

    //도보 관련 이동수단
    restsapce("휴게 공간", "Rest space"),
    stair_normal("계단 정상", "Stair normal"),
    stair_broken("계단 파손", "Stair broken"),
    lift("엘리베이터", "Lift"),

    //표시
    sign_disabled_toilet("장애인 화장실 표시", "Disabled toilet sign"),
    braille_sign("점자 표시", "Braille sign"),

    //휴식 공간 및 인프라
    resting_place_roof("휴게 공간 지붕", "Resting place roof"),
    chair_multi("의자 다수", "Chair multi"),
    chair_back("등받이 의자", "Chair back"),
    chair_handle("핸들 의자", "Chair handle"),
    beverage_vending_machine("음료 자판기", "Beverage vending machine"),
    trash_can("쓰레기통", "Trash can"),

    ;
    private final String korean;
    private final String english;

    public String toKorean() {
        return korean;
    }

    public String toEnglish() {
        return english;
    }
}