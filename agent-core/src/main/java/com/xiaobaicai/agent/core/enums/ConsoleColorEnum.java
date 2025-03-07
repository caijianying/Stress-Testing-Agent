package com.xiaobaicai.agent.core.enums;

/**
 * @author liguang
 * @date 2022/9/20 星期二 5:21 下午
 */
public enum ConsoleColorEnum {

    DEFAULT("39"),

    BLACK("30"),

    RED("31"),

    GREEN("32"),

    YELLOW("33"),

    BLUE("34"),

    MAGENTA("35"),

    CYAN("36"),

    WHITE("37"),

    BRIGHT_BLACK("90"),

    BRIGHT_RED("91"),

    BRIGHT_GREEN("92"),

    BRIGHT_YELLOW("93"),

    BRIGHT_BLUE("94"),

    BRIGHT_MAGENTA("95"),

    BRIGHT_CYAN("96"),

    BRIGHT_WHITE("97"),

    NORMAL("0"),

    BOLD("1"),

    FAINT("2"),

    ITALIC("3"),

    UNDERLINE("4");

    private String code;

    ConsoleColorEnum(String code){
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
