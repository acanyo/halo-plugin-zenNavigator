export interface Metadata {
  name: string;
  generateName?: string;
  labels?: {
    [key: string]: string;
  } | null;
  annotations?: {
    [key: string]: string;
  } | null;
  version?: number | null;
  creationTimestamp?: string | null;
  deletionTimestamp?: string | null;
}

export interface NavGroupSpec {
  name: string;
  icon?: string;
  inTopMenu?: boolean;
  priority?: number;
}

export interface NavGroupStatus {
  siteCount: number;
}

export interface NavGroup {
  spec: NavGroupSpec;
  apiVersion: string;
  kind: string;
  metadata: Metadata;
  status?: NavGroupStatus;
}

export interface NavGroupList {
  page: number;
  size: number;
  total: number;
  items: Array<NavGroup>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface NavSiteSpec {
  name: string;
  url: string;
  icon?: string;
  groupName: string;
  description?: string;
}

export interface NavSite {
  spec: NavSiteSpec;
  apiVersion: string;
  kind: string;
  metadata: Metadata;
}

export interface NavSiteList {
  page: number;
  size: number;
  total: number;
  totalPages?: number;
  items: Array<NavSite>;
  first?: boolean;
  last?: boolean;
  hasNext?: boolean;
  hasPrevious?: boolean;
}
