package com.test.dgraph;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc;
import io.dgraph.DgraphGrpc.DgraphStub;
import io.dgraph.DgraphProto.Mutation;
import io.dgraph.DgraphProto.Operation;
import io.dgraph.DgraphProto.Response;
import io.dgraph.Transaction;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestBatchImportData {
  private static final String TEST_HOSTNAME = "localhost";
  private static final int TEST_PORT = 9080;

  //创建dgraph
  private static DgraphClient createDgraphClient(boolean withAuthHeader) {
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress(TEST_HOSTNAME, TEST_PORT).usePlaintext(true).build();
    DgraphStub stub = DgraphGrpc.newStub(channel);

    if (withAuthHeader) {
      Metadata metadata = new Metadata();
      metadata.put(
          Metadata.Key.of("auth-token", Metadata.ASCII_STRING_MARSHALLER), "the-auth-token-value");
      stub = MetadataUtils.attachHeaders(stub, metadata);
    }

    return new DgraphClient(stub);
  }

  public static void main(final String[] args) {
    DgraphClient dgraphClient = createDgraphClient(false);

    //初始化,删除所有的模式
	dgraphClient.alter(Operation.newBuilder().setDropAll(true).build());

    //设置模式,添加索引
    String schema = "name: string @index(term) .";//name这个谓语含有一个string类型的索引
    Operation op = Operation.newBuilder().setSchema(schema).build();
    dgraphClient.alter(op);

    Gson gson = new Gson(); // For JSON encode/decode

    Transaction txn = dgraphClient.newTransaction();
    try {
    	StringBuilder json = new StringBuilder();
    	
    	json.append("{\"rows\":[");
    	for(int i=0;i<50000;i++){
    	      //初始化数据
    	      Person p = new Person();
    	      p.name = String.valueOf(i);
    	      //序列化为string
    	      if(i!=49999){
    	    	  json.append(gson.toJson(p)+",");
    	      }else{
    	    	  json.append(gson.toJson(p));
    	      }
    	}
    	 json.append("]}");

    	 // 运行mutation(数据突变--修改存储在Dgraph中的图结构 )
    	 Mutation mu = Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(json.toString())).build();
    	txn.mutate(mu);
    	txn.commit();

    } finally {
      txn.discard();
    }
    
    Transaction txn2 = dgraphClient.newTransaction();
    try {
    	StringBuilder json = new StringBuilder();
    	
    	json.append("{\"rows\":[");
    	for(int i=50000;i<100000;i++){
    	      //初始化数据
    	      Person p = new Person();
    	      p.name = String.valueOf(i);
    	      //序列化为string
    	      if(i!=99999){
    	    	  json.append(gson.toJson(p)+",");
    	      }else{
    	    	  json.append(gson.toJson(p));
    	      }
    	}
    	 json.append("]}");

    	 // 运行mutation(数据突变--修改存储在Dgraph中的图结构 )
    	 Mutation mu = Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(json.toString())).build();
    	txn2.mutate(mu);
    	txn2.commit();

    } finally {
      txn2.discard();
    }
    
    
    Transaction txn3 = dgraphClient.newTransaction();
    try {
    	StringBuilder json = new StringBuilder();
    	
    	json.append("{\"rows\":[");
    	for(int i=100000;i<150000;i++){
    	      //初始化数据
    	      Person p = new Person();
    	      p.name = String.valueOf(i);
    	      //序列化为string
    	      if(i!=149999){
    	    	  json.append(gson.toJson(p)+",");
    	      }else{
    	    	  json.append(gson.toJson(p));
    	      }
    	}
    	 json.append("]}");

    	 // 运行mutation(数据突变--修改存储在Dgraph中的图结构 )
    	 Mutation mu = Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(json.toString())).build();
    	 txn3.mutate(mu);
    	 txn3.commit();

    } finally {
    	txn3.discard();
    }
    
    Transaction txn4 = dgraphClient.newTransaction();
    try {
    	StringBuilder json = new StringBuilder();
    	
    	json.append("{\"rows\":[");
    	for(int i=150000;i<200000;i++){
    	      //初始化数据
    	      Person p = new Person();
    	      p.name = String.valueOf(i);
    	      //序列化为string
    	      if(i!=199999){
    	    	  json.append(gson.toJson(p)+",");
    	      }else{
    	    	  json.append(gson.toJson(p));
    	      }
    	}
    	 json.append("]}");

    	 // 运行mutation(数据突变--修改存储在Dgraph中的图结构 )
    	 Mutation mu = Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(json.toString())).build();
    	 txn4.mutate(mu);
    	 txn4.commit();

    } finally {
    	txn4.discard();
    }
    
    // 查询
    long time1 = System.currentTimeMillis();
    String query =
        "query all($a: string){\n" + "all(func: eq(name, $a)) {\n" + "    name\n" + "  }\n" + "}";
    Map<String, String> vars = Collections.singletonMap("$a", "91999");
    Response res = dgraphClient.newTransaction().queryWithVars(query, vars);
    // 反序列化
    People ppl = gson.fromJson(res.getJson().toStringUtf8(), People.class);

    // 输出结果
    System.out.printf("people found: %d\n", ppl.all.size());
    long time2 = System.currentTimeMillis();
    System.out.println(time2-time1);
    //ppl.all.forEach(person -> System.out.println(person.name));
  }

  static class Person {
    String name;

    Person() {}
  }

  static class People {
    List<Person> all;

    People() {}
  }
}