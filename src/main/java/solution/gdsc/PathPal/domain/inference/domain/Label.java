package solution.gdsc.PathPal.domain.inference.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Label {
    // 도보 상태
//    flatness_A("평탄도 매우 좋음", "Flatness very good"),
//    flatness_B("평탄도 좋음", "Flatness good"),
//    flatness_C("평탄도 보통", "Flatness very good"),
    flatness_D("비포장 도보", "Flatness very good"),
    flatness_E("비포장 도보", "Flatness very good"),
    walkway_paved("포장 도보", "Paved walkway"),
    walkway_block("블럭 도보", "Block walkway"),
    paved_state_broken("도보 파손", "Paved walkway broken"),
    paved_state_normal("포장 도보", "Paved walkway normal"),
    block_state_normal("블럭 도보", "Block walkway normal"),
    block_kind_bad("파손된 블럭 도보", "Block walkway bad"),
    //block_kind_good("블럭 도보", "Block walkway good"),
    outcurb_rectangle("보도턱", "Rectangle crosswalk"),
    outcurb_slide("보도턱", "Slide crosswalk"),
    outcurb_rectengle_broken("파손된 보도턱", "Rectangle crosswalk broken"),

    // 도보 위험 요소
    sidegap_out("도보 틈새", "Paved walkway outside side gap"),
    steepramp("경사로", "Steep ramp"),
    bollard("볼라드", "Bollard"),
    pole("기둥", "pole"),
    barricade("바리케이트", "barricade"),

    //특수 도보
    brailleblock_dot("점자 블럭", "Braille block"),
    brailleblock_line("점자 블럭", "Braille block"),
    brailleblock_dot_broken("파손된 점자 블럭", "Braille block broken"),
    brailleblock_line_broken("파손된 점자 블럭", "Braille block broken"),
    bicycleroad_broken("파손된 자전거 도로", "Bicycle road broken"),
    bicycleroad_normal("자전거 도로", "Bicycle road normal"),

    //횡단 보도
    planecrosswalk_broken("횡단보도", "Plane crosswalk broken"),
    planecrosswalk_normal("횡단보도", "Plane crosswalk normal"),

    //도보 관련 이동수단
    restsapce("휴게 공간", "Rest space"),
    stair_normal("계단", "Stair normal"),
    stair_broken("파손된 계단", "Stair broken"),
    lift("엘리베이터", "Lift"),

    //표시
    sign_disabled_toilet("장애인 화장실", "Disabled toilet sign"),
    braille_sign("점자 안내", "Braille sign"),

    //휴식 공간 및 인프라
    resting_place_roof("휴게 공간", "Resting place roof"),
    chair_multi("다인용 벤치", "Chair multi"),
    chair_back("벤치", "Chair back"),
    chair_handle("벤치", "Chair handle"),
    beverage_vending_machine("음료 자판기", "Beverage vending machine"),
    trash_can("쓰레기통", "Trash can"),


    temporary_label("[임시 라벨]", "[temporary label]"), // TODO

    green_traffic("초록불", "Green traffic"),
    stair("계단", "stair"),

    ;
    private final String korean;
    private final String english;

    public static Label fromName(String name) {
        for (Label label : Label.values()) {
            if (label.name().equals(name)) {
                return label;
            }
        }
        return temporary_label;
    }

    public String toKorean() {
        return korean;
    }

    public String toEnglish() {
        return english;
    }
}