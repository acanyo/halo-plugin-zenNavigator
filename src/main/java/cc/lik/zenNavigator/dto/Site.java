package cc.lik.zenNavigator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Site {
    private String name;
    private String owner_id;
    private String url;
    private String icon;
    private String description;
} 