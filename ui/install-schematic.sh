#!/usr/bin/env bash

set -e

cd schematics/angular-cqrs
npm link
cd ../..
npm link angular-cqrs
