{
  inputs = {
    nixpkgs = {
      url = "github:nixos/nixpkgs/nixpkgs-unstable";
    };
    flake-utils = {
      url = "github:numtide/flake-utils";
    };
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
        thrift_0_21_0 = pkgs.thrift.overrideAttrs (old: let version = "0.21.0"; in {
          inherit version;
          src = pkgs.fetchFromGitHub {
            owner = "apache";
            repo = "thrift";
            tag = "v${version}";
            hash = "sha256-OF/pFG8OXROsyYGf6jgfVTYTrTc8UB5QJh4gbostFfU=";
          };
          doCheck = false;
          patches = [];
          postPatch = "";
        });
      in
      {
        devShells = with pkgs; {
          default = mkShell {
            buildInputs = [
              thrift_0_21_0
              protobuf
              maven
              jdk8
            ];
          };
        };
      }
    );
}
