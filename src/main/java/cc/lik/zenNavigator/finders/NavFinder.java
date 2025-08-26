package cc.lik.zenNavigator.finders;

import cc.lik.zenNavigator.vo.NavGroupVo;
import cc.lik.zenNavigator.vo.NavSiteVo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;

public interface NavFinder {
    /**
     * 列出所有导航分类
     *
     * @return 导航分类的Flux流
     */
    Flux<NavGroupVo> listAllGroups();

    /**
     * 分页列出导航分类
     *
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    Mono<ListResult<NavGroupVo>> listGroups(Integer page, Integer size);

    /**
     * 根据分类名称查找导航分类
     *
     * @param groupName 分类名称
     * @return 导航分类
     */
    Mono<NavGroupVo> findGroupByName(String groupName);

    /**
     * 列出所有导航站点
     *
     * @return 导航站点的Flux流
     */
    Flux<NavSiteVo> listAllSites();

    /**
     * 分页列出导航站点
     *
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    Mono<ListResult<NavSiteVo>> listSites(Integer page, Integer size);

    /**
     * 根据分类名称列出站点
     *
     * @param groupName 分类名称
     * @return 该分类下的站点
     */
    Flux<NavSiteVo> listSitesByGroup(String groupName);

    /**
     * 根据站点名称查找站点
     *
     * @param siteName 站点名称
     * @return 导航站点
     */
    Mono<NavSiteVo> findSiteByName(String siteName);

    /**
     * 获取完整的导航数据（分类+站点）
     *
     * @return 包含分类和站点的完整导航数据
     */
    Flux<NavGroupVo> getFullNavigation();
}
