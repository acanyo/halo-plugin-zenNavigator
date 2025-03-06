package cc.lik.zenNavigator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private String id;
    private String name;
    private String icon;
    private boolean inTopMenu;
    private List<Subcategory> subcategories;
}