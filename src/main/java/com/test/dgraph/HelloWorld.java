package com.test.dgraph;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

public class HelloWorld {

	public static void main(String[] args) {

		// 链接地址
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9080).usePlaintext(true).build();
		DgraphStub stub = DgraphGrpc.newStub(channel);
		// DgraphClient dgraphClient = new DgraphClient(Collections.singletonList(stub));
		DgraphClient dgraphClient = new DgraphClient(stub);
		// 设置schema
		//String schema = "name: string @index(exact) .";
		//Operation op = Operation.newBuilder().setSchema(schema).build();
		//dgraphClient.alter(op);
		// 删除所有数据
		// dgraphClient.alter(Operation.newBuilder().setDropAll(true).build());

		/**
		 * mutate 增加名字
		 */
		// 事务
		//Transaction txn = dgraphClient.newTransaction();
		// Create data
		//Person p = new Person();
		//p.name = "Alice";
		// Serialize it
		
		//String json = gson.toJson(p);
		// Run mutation
		//Mutation mu = Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(json.toString())).build();
		//txn.mutate(mu);
		
		/**
		 * 一次添加索引,一次数据突变后,即可自己查询
		 * query
		 */
		
		// Query
		Gson gson = new Gson();
		String query =
				"query all($a: string){\n"+
				 "me(func: has(starring)) @filter(ge(release_date, $a)) {\n"+
				    "name\n"+
				  "}\n"+
				"}\n";
				
		query =
				"query all($a: string){\n"+
				 "all(func:allofterms(name, $a)) {\n"+
				    "name\n"+
				  "}\n"+
				"}\n";	
		
//		
//		query =
//				"query all($a: string){\n"+
//				 "rows(func:eq(pid,$a)) {\n"+
//				    "id,name,organCode\n"+
//				  "}\n"+
//				"}\n";	
				
//		"query all($a: string){\n" +
//		"  all(func: eq(name, $a)) {\n" +
//		"    name\n" +
//		"  }\n" +
//		"}\n";
		Map<String, String> vars = Collections.singletonMap("$a", "Star Wars");
		Response res = dgraphClient.newReadOnlyTransaction().queryWithVars(query, vars);
		System.out.println(res.getJson().toStringUtf8());
		// Deserialize
		//People ppl = gson.fromJson(res.getJson().toStringUtf8(), People.class);

		// Print results
		//System.out.printf("people found: %d\n", ppl.all.size());
		//ppl.all.forEach(person -> System.out.println(person.name));

	}

}
