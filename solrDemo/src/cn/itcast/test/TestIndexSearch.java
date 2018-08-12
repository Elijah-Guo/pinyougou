package cn.itcast.test;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

/**
 * 索引和文档查询
 */
public class TestIndexSearch {

    @Test
    public void testIndexDelete() throws Exception {
        //1. 创建和solr连接, 这里是连接默认实例, 也就是collection1实例
        //如果连接其他实例 http://192.168.200.128:8080/solr/实例名称
        SolrServer sorlServer = new HttpSolrServer("http://192.168.200.128:8080/solr/");

        //2. 创建查询条件对象
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");

        //3. 查询并返回响应
        QueryResponse queryResponse = sorlServer.query(query);
        //4. 获取查询到的文档的结果集
        SolrDocumentList results = queryResponse.getResults();

        if(results != null){
            //获取到的查询的总数
            System.out.println("=====count=====" + results.getNumFound());
            for (SolrDocument doc : results) {
                System.out.println("====id====" + doc.get("id"));
                System.out.println("====product_name====" + doc.get("product_name"));
                System.out.println("=====product_price===" + doc.get("product_price"));
                System.out.println("====product_catalog_name====" + doc.get("product_catalog_name"));
                System.out.println("========" + doc.get("product_picture"));
                System.out.println("===================================================================" );
            }
        }
    }
}
