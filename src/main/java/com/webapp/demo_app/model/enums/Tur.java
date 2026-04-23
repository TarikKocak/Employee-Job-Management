package com.webapp.demo_app.model.enums;
import java.util.List;


public enum Tur {
    BERABER,
    YALNIZ,
    COWORK,
    ASSEMBLY,
    DESIGN,
    CONSULTING;

    private static final List<Tur> STANDARD_TYPES = List.of(BERABER,YALNIZ,COWORK);
    private static final List<Tur> MASTER_EXTRA_TYPES = List.of(ASSEMBLY,DESIGN,CONSULTING);

    public static List<Tur> allowedForTitle(EmployeeeTitle title){
        if(title==EmployeeeTitle.MASTER){
            return List.of(BERABER,YALNIZ,COWORK,ASSEMBLY,DESIGN,CONSULTING);
        }
        return STANDARD_TYPES;
    }

    public boolean isMasterOnly(){
        return MASTER_EXTRA_TYPES.contains(this);
    }
}
