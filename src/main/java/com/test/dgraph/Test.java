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

public class Test {
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

    // 初始化,删除所有的模式
    dgraphClient.alter(Operation.newBuilder().setDropAll(true).build());

    // 设置模式,添加索引
    String schema = "name: string @index(exact) .";//name这个谓语含有一个string类型的索引
    Operation op = Operation.newBuilder().setSchema(schema).build();
    dgraphClient.alter(op);

    Gson gson = new Gson(); // For JSON encode/decode

    Transaction txn = dgraphClient.newTransaction();
    try {
      //初始化数据
      Person p = new Person();
      p.name = "Alice";

      //序列化为string
      String json = gson.toJson(p);

      // 运行mutation(数据突变--修改存储在Dgraph中的图结构 )
      Mutation mu =
          Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(json.toString())).build();
      txn.mutate(mu);
      txn.commit();

    } finally {
      txn.discard();
    }
    // 查询
    String query =
        "query all($a: string){\n" + "all(func: eq(name, $a)) {\n" + "    name\n" + "  }\n" + "}";
    Map<String, String> vars = Collections.singletonMap("$a", "Alice");
    Response res = dgraphClient.newTransaction().queryWithVars(query, vars);

    // 反序列化
    People ppl = gson.fromJson(res.getJson().toStringUtf8(), People.class);

    // 输出结果
    System.out.printf("people found: %d\n", ppl.all.size());
    ppl.all.forEach(person -> System.out.println(person.name));
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