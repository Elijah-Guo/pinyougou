package cn.itcast.test;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

/**
 * 索引和文档管理(增删改)
 */
public class TestIndexManager {

    /**
     * solr 中没有专门的修改方法, 都是根据ID去索引库查询, 如果查询到了, 就将原来的文档对象删除, 将新的文档对象添加进去
     * 这就是修改操作, 如果根据id查询不到文档对象, 则直接将新的文档添加进去, 这就是添加操作.
     * @throws Exception
     */
    @Test
    public void testIndexCreateAndUpdate() throws Exception{
        //1. 创建和solr连接, 这里是连接默认实例, 也就是collection1实例
        //如果连接其他实例 http://192.168.200.128:8080/solr/实例名称
        SolrServer sorlServer = new HttpSolrServer("http://192.168.200.128:8080/solr/");
        //2. 创建文档对象
        SolrInputDocument doc = new SolrInputDocument();
        //3. 添加域, 域要先定义后适应, 还有一定要有id主键域
        doc.addField("id", "10000");
        doc.addField("title", "西游记");
        //4. 添加
        sorlServer.add(doc);
        //5. 提交
        sorlServer.commit();
    }

    @Test
    public void testIndexDelete() throws Exception{
        //1. 创建和solr连接, 这里是连接默认实例, 也就是collection1实例
        //如果连接其他实例 http://192.168.200.128:8080/solr/实例名称
        SolrServer sorlServer = new HttpSolrServer("http://192.168.200.128:8080/solr/");
        //2. 根据ID删除
        //sorlServer.deleteById("10000");
        //根据查询条件删除
        sorlServer.deleteByQuery("*:*");
        //3. 提交
        sorlServer.commit();
    }
}
