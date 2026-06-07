package com.jizhang.smartledger.data.local

import com.jizhang.smartledger.data.local.entity.CategoryEntity

/** Default category plus its local merchant keyword rules. */
data class CategorySeed(
    val category: CategoryEntity,
    val rules: List<String>
)

/** Builds the MVP's default categories and local keyword rules. */
fun defaultCategorySeeds(): List<CategorySeed> {
    return listOf(
        seed("餐饮", "#E4572E", "restaurant", 10, "餐", "饭", "咖啡", "奶茶", "美团", "饿了么"),
        seed("交通", "#2E86AB", "directions_car", 20, "滴滴", "地铁", "公交", "高德", "铁路"),
        seed("购物", "#7B61FF", "shopping_bag", 30, "淘宝", "京东", "拼多多", "超市", "便利店"),
        seed("住房", "#1B998B", "home", 40, "房租", "物业", "水费", "电费", "燃气"),
        seed("娱乐", "#F4A261", "sports_esports", 50, "电影", "会员", "游戏", "KTV"),
        seed("医疗", "#D7263D", "local_hospital", 60, "医院", "药", "医保", "门诊"),
        seed("学习", "#3D5A80", "school", 70, "课程", "书", "培训", "得到"),
        seed("转账", "#6C757D", "swap_horiz", 80, "转账", "红包"),
        seed("人情", "#B56576", "volunteer_activism", 90, "礼金", "份子", "请客"),
        seed("其他", "#495057", "more_horiz", 100)
    )
}

private fun seed(
    name: String,
    color: String,
    icon: String,
    sortOrder: Int,
    vararg rules: String
): CategorySeed {
    return CategorySeed(
        category = CategoryEntity(name = name, color = color, icon = icon, sortOrder = sortOrder),
        rules = rules.toList()
    )
}
