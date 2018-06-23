# myBatisGenerator
Mybatis Generator - generate customized comment

includes one Project with two Modules:

Module1 comment-generator: MyCommentGenerator extends DefaultCommentGenerator;

Module2 mybatis-generator: depends on the comment-generator, and generate domain, dao, mappers;

Module3 customize-generator: independent, could generate domain, dao, mappers.
