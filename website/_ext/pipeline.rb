require File.expand_path '../haml/filters/asciidoc.rb', __FILE__

Awestruct::Extensions::Pipeline.new do
	helper Awestruct::Extensions::Partial
	helper Awestruct::Extensions::Relative
end
