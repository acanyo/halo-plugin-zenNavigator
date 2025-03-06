package cc.lik.zenNavigator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subcategory {
    private String id;
    private String owner_id;
    private String name;
    private String icon;
    private List<Site> sites;
} 