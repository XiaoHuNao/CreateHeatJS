package com.xiaohunao.create_heat_js.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;


public class HeatRelations {
    private final Map<String, List<String>> relations = new HashMap<>();
    private final Map<String, List<HeatData.SatisfyRule>> conditionalRelations = new HashMap<>();


    public void addLinks(String providerName, Collection<String> requirementNames) {
        relations.computeIfAbsent(providerName, k -> new ArrayList<>()).addAll(requirementNames);
    }

    public void addConditionalLink(String providerName, String requirementName, Predicate<HeatRecipeContext> predicate) {
        conditionalRelations.computeIfAbsent(providerName, k -> new ArrayList<>())
            .add(new HeatData.SatisfyRule(requirementName, predicate));
    }


    public boolean matches(String providerName, String requirementName, HeatRecipeContext context) {
        // 1. 直接匹配
        if (providerName.equals(requirementName)) {
            return true;
        }

        // 2. 检查直接关系
        List<String> directRequirements = relations.get(providerName);
        if (directRequirements != null) {
            for (String directReq : directRequirements) {
                if (matches(directReq, requirementName, context)) {
                    return true;
                }
            }
        }

        // 3. 检查条件关系
        List<HeatData.SatisfyRule> rules = conditionalRelations.get(providerName);
        if (rules != null) {
            for (HeatData.SatisfyRule rule : rules) {
                if (rule.getPredicate().test(context)) {
                    if (matches(rule.getRequirement(), requirementName, context)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
