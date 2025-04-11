export interface ChildItem {
  id?: number | string;
  name?: string;
  icon?: any;
  children?: ChildItem[];
  item?: any;
  url?: any;
  color?: string;
}

export interface MenuItem {
  heading?: string;
  name?: string;
  icon?: any;
  id?: number;
  to?: string;
  items?: MenuItem[];
  children?: ChildItem[];
  url?: any;
}

import { uniqueId } from "lodash";

const SidebarContent: MenuItem[] = [
  {
    heading: "TRANG CHỦ",
    children: [
      {
        name: "Danh sách banner",
        icon: "solar:bedside-table-3-linear",
        id: uniqueId(),
        url: "/",
      },
    ],
  },
  {
    heading: "DANH MỤC",
    children: [
      {
        name: "Danh sách banner",
        icon: "solar:bedside-table-3-linear",
        id: uniqueId(),
        url: "/banner",
      },
      {
        name: "Danh sách Category",
        icon: "solar:text-circle-outline",
        id: uniqueId(),
        url: "/category",
      },
      {
        name: "Danh sách item",
        icon: "solar:bedside-table-3-linear",
        id: uniqueId(),
        url: "/items",
      },
      {
        name: "Danh sách user",
        icon: "solar:bedside-table-3-linear",
        id: uniqueId(),
        url: "/users",
      },
      
    ],
  },
  // {
  //   heading: "AUTH",
  //   children: [
  //     {
  //       name: "Login",
  //       icon: "solar:login-2-linear",
  //       id: uniqueId(),
  //       url: "/auth/login",
  //     },
  //     {
  //       name: "Register",
  //       icon: "solar:shield-user-outline",
  //       id: uniqueId(),
  //       url: "/auth/register",
  //     },
  //   ],
  // },
  // {
  //   heading: "EXTRA",
  //   children: [
  //     {
  //       name: "Icons",
  //       icon: "solar:smile-circle-outline",
  //       id: uniqueId(),
  //       url: "/icons/solar",
  //     },
  //     {
  //       name: "Sample Page",
  //       icon: "solar:notes-minimalistic-outline",
  //       id: uniqueId(),
  //       url: "/sample-page",
  //     },
  //   ],
  // },
];

export default SidebarContent;
