package com.yalu.india.data.sample

import com.yalu.india.network.models.AdDetailDto
import com.yalu.india.network.models.AdSummaryDto
import com.yalu.india.network.models.CategoryDto
import com.yalu.india.network.models.CityDto
import com.yalu.india.network.models.SellerDto
import com.yalu.india.network.models.SubcategoryDto

/**
 * Clearly labeled mock/sample data for UI previews and offline resilience.
 * Note: Used only when live API responses are unreachable during development.
 */
object SampleData {

    val sampleCities = listOf(
        CityDto(1, "All India", "all-india"),
        CityDto(2, "Hyderabad", "hyderabad"),
        CityDto(3, "Bangalore", "bangalore"),
        CityDto(4, "Mumbai", "mumbai"),
        CityDto(5, "Delhi NCR", "delhi-ncr"),
        CityDto(6, "Chennai", "chennai"),
        CityDto(7, "Pune", "pune"),
        CityDto(8, "Kolkata", "kolkata")
    )

    val sampleCategories = listOf(
        CategoryDto(
            id = 1,
            title = "Mobiles & Tablets",
            slug = "mobiles-tablets",
            icon = "phone_android",
            subcategories = listOf(
                SubcategoryDto(id = 101, title = "Smartphones", slug = "smartphones", parentId = 1L),
                SubcategoryDto(id = 102, title = "Tablets & iPads", slug = "tablets", parentId = 1L),
                SubcategoryDto(id = 103, title = "Accessories", slug = "accessories", parentId = 1L)
            )
        ),
        CategoryDto(
            id = 2,
            title = "Vehicles & Bikes",
            slug = "vehicles",
            icon = "directions_car",
            subcategories = listOf(
                SubcategoryDto(id = 201, title = "Cars", slug = "cars", parentId = 2L),
                SubcategoryDto(id = 202, title = "Motorcycles & Scooters", slug = "motorcycles", parentId = 2L),
                SubcategoryDto(id = 203, title = "Commercial Vehicles", slug = "commercial", parentId = 2L)
            )
        ),
        CategoryDto(
            id = 3,
            title = "Electronics & Appliances",
            slug = "electronics",
            icon = "tv",
            subcategories = listOf(
                SubcategoryDto(id = 301, title = "Laptops & Computers", slug = "laptops", parentId = 3L),
                SubcategoryDto(id = 302, title = "TVs & Audio", slug = "tvs", parentId = 3L),
                SubcategoryDto(id = 303, title = "Kitchen Appliances", slug = "kitchen-appliances", parentId = 3L)
            )
        ),
        CategoryDto(
            id = 4,
            title = "Real Estate & Properties",
            slug = "real-estate",
            icon = "home",
            subcategories = listOf(
                SubcategoryDto(id = 401, title = "Apartments for Rent", slug = "rent", parentId = 4L),
                SubcategoryDto(id = 402, title = "Plots & Land", slug = "plots", parentId = 4L),
                SubcategoryDto(id = 403, title = "Commercial Office Space", slug = "office", parentId = 4L)
            )
        ),
        CategoryDto(
            id = 5,
            title = "Jobs & Services",
            slug = "jobs-services",
            icon = "work",
            subcategories = listOf(
                SubcategoryDto(id = 501, title = "Full-Time Jobs", slug = "jobs", parentId = 5L),
                SubcategoryDto(id = 502, title = "Home Repairs & Cleaning", slug = "home-repairs", parentId = 5L),
                SubcategoryDto(id = 503, title = "IT & Freelance Services", slug = "it-services", parentId = 5L)
            )
        ),
        CategoryDto(
            id = 6,
            title = "Fashion & Beauty",
            slug = "fashion-beauty",
            icon = "checkroom",
            subcategories = listOf(
                SubcategoryDto(id = 601, title = "Men\'s Clothing", slug = "mens-clothing", parentId = 6L),
                SubcategoryDto(id = 602, title = "Women\'s Clothing", slug = "womens-clothing", parentId = 6L),
                SubcategoryDto(id = 603, title = "Jewelry & Watches", slug = "jewelry", parentId = 6L)
            )
        )
    )

    val sampleFeaturedAds = listOf(
        AdSummaryDto(
            id = "101",
            title = "[Sample] iPhone 15 Pro 128GB - Pristine Condition, Bill & Box",
            price = 92000.0,
            formattedPrice = "₹ 92,000",
            location = "Hitech City, Hyderabad",
            image = "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600",
            category = "Mobiles & Tablets",
            categoryId = 1,
            isFeatured = true,
            isNegotiable = true,
            createdAt = "2 hours ago"
        ),
        AdSummaryDto(
            id = "102",
            title = "[Sample] Royal Enfield Hunter 350 (2023) - 8,500 km Only",
            price = 145000.0,
            formattedPrice = "₹ 1,45,000",
            location = "Indiranagar, Bangalore",
            image = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=600",
            category = "Vehicles & Bikes",
            categoryId = 2,
            isFeatured = true,
            isNegotiable = false,
            createdAt = "5 hours ago"
        ),
        AdSummaryDto(
            id = "103",
            title = "[Sample] MacBook Air M2 16GB / 512GB SSD Midnight",
            price = 88000.0,
            formattedPrice = "₹ 88,000",
            location = "Bandra West, Mumbai",
            image = "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600",
            category = "Electronics & Appliances",
            categoryId = 3,
            isFeatured = true,
            isNegotiable = true,
            createdAt = "1 day ago"
        ),
        AdSummaryDto(
            id = "104",
            title = "[Sample] 2 BHK Luxury Furnished Apartment with Balcony",
            price = 32000.0,
            formattedPrice = "₹ 32,000 / mo",
            location = "Gachibowli, Hyderabad",
            image = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=600",
            category = "Real Estate & Properties",
            categoryId = 4,
            isFeatured = true,
            isNegotiable = false,
            createdAt = "2 days ago"
        )
    )

    fun getSampleDetail(id: String): AdDetailDto {
        return AdDetailDto(
            id = id,
            title = "[Sample Preview] iPhone 15 Pro 128GB Titanium - Under Apple Care Warranty",
            price = 92000.0,
            formattedPrice = "₹ 92,000",
            description = "Selling 6-month-old Apple iPhone 15 Pro in Natural Titanium.\n\n• Condition: 10/10, scratchless\n• Battery Health: 98%\n• Included: Original Box, Braided Type-C Cable, Invoice with Apple Warranty valid till Dec 2026.\n\nReason for selling: Upgraded to higher storage capacity. Genuine buyers can inspect in person at Hitech City.",
            location = "Hitech City, Hyderabad, Telangana",
            images = listOf(
                "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=800",
                "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800",
                "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=800"
            ),
            category = "Mobiles & Tablets",
            categoryId = 1,
            isFeatured = true,
            isNegotiable = true,
            viewsCount = 342,
            seller = SellerDto(
                id = "42",
                name = "Yalu Verified Seller",
                phone = "+919876543210",
                whatsapp = "919876543210",
                avatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
                memberSince = "Member since Jan 2024",
                isVerified = true
            ),
            createdAt = "12 Oct 2026",
            shareUrl = "https://www.yalu.in/ad/$id"
        )
    }
}
