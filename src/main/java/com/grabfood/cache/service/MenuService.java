package com.grabfood.cache.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    /**
     * GIẢI THÍCH LÝ DO DÙNG @CacheEvict THAY VÌ @CachePut:
     * - Chiến lược Cache-Aside (Cập nhật DB trước, Xóa cache sau) giúp đảm bảo tính nhất quán dữ liệu tốt hơn.
     * - Khi sử dụng @CachePut, nếu có 2 luồng (thread) cùng cập nhật đồng thời, có thể xảy ra Race Condition 
     *   khiến dữ liệu ghi vào Cache bị sai lệch so với Database (dữ liệu cũ ghi đè lên dữ liệu mới trong cache).
     * - Việc dùng @CacheEvict sẽ xóa hẳn cache. Lần đọc (GET) tiếp theo sẽ buộc phải lấy dữ liệu mới nhất 
     *   từ Database rồi nạp lại vào Cache, loại bỏ hoàn toàn nguy cơ Race Condition và lỗi Stale Data.
     */
    @CacheEvict(value = "menuCache", key = "#menu.id")
    public void updateMenu(Menu menu) {
        // Bước 2: Thực hiện cập nhật database trước
        menuRepository.save(menu);
        
        // Bước 3: Cache sẽ tự động bị xóa (evict) nhờ annotation @CacheEvict sau khi method thực thi xong
    }
}

// Các class phụ trợ (Mock) để file không báo lỗi cú pháp cơ bản
class Menu {
    private Long id;
    private String dishName;
    private Double price;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // getters/setters...
}

interface MenuRepository {
    void save(Menu menu);
}
