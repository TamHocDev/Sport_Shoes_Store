"use client";

import { useState, useEffect } from "react";

interface Banner {
  id?: string;
  url: string;
}

export default function BannerPage() {
  const [banners, setBanners] = useState<Banner[]>([]);
  const [newBanner, setNewBanner] = useState<Banner>({
    url: "",
  });
  const [editingBanner, setEditingBanner] = useState<Banner | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [bannerToDelete, setBannerToDelete] = useState<string | null>(null);

  useEffect(() => {
    fetch("/api/banner")
    .then((res) => {
      console.log(res); // Log the full response here
      return res.json();
    })
    .then((data) => {
      console.log("Dữ liệu banner:", data);
      setBanners(data);
    })
    .catch((error) => console.error("Error fetching banner data:", error));  
  }, []);

  const openAddModal = () => {
    setEditingBanner(null);
    setNewBanner({
      url: "",
    });
    setIsModalOpen(true);
  };

  const openEditModal = (banner: Banner) => {
    setEditingBanner(banner);
    setNewBanner({ ...banner });
    setIsModalOpen(true);
  };

  const openDeleteModal = (id: string) => {
    setBannerToDelete(id);
    setIsDeleteModalOpen(true);
  };

  const handleSave = async () => {
    const method = editingBanner ? "PUT" : "POST";
    const url = editingBanner ? `/api/banner?id=${editingBanner.id}` : "/api/banner";

    try {
      const res = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newBanner),
      });

      if (res.ok) {
        const data = await res.json();
        setBanners((prev) =>
          editingBanner
            ? prev.map((b) => (b.id === data.id ? data : b))
            : [...prev, data]
        );
        setIsModalOpen(false);
        setNewBanner({
          url: "",
        });
        setEditingBanner(null);
      } else {
        console.error("Error saving banner:", await res.text());
      }
    } catch (error) {
      console.error("Error saving banner:", error);
    }
  };

  const handleDelete = async () => {
    if (!bannerToDelete) return;
    
    try {
      const res = await fetch(`/api/banner?id=${bannerToDelete}`, {
        method: "DELETE",
      });
      
      if (res.ok) {
        setBanners((prev) => prev.filter((b) => b.id !== bannerToDelete));
        setIsDeleteModalOpen(false);
        setBannerToDelete(null);
      } else {
        console.error("Error deleting banner:", await res.text());
      }
    } catch (error) {
      console.error("Error deleting banner:", error);
    }
  };

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Quản lý Banner</h1>
        <button
          onClick={openAddModal}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md transition-colors duration-200 flex items-center"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-5 w-5 mr-2"
            viewBox="0 0 20 20"
            fill="currentColor"
          >
            <path
              fillRule="evenodd"
              d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z"
              clipRule="evenodd"
            />
          </svg>
          Thêm Banner
        </button>
      </div>

      {/* Banner Gallery */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {banners.length > 0 ? (
          banners.map((banner) => (
            <div key={banner.id} className="bg-white rounded-lg shadow-md overflow-hidden">
              <div className="relative aspect-[16/9]">
                <img 
                  src={banner.url} 
                  alt="Banner" 
                  className="w-full h-full object-cover"
                />
              </div>
              <div className="p-4 flex justify-end space-x-4">
                <button
                  onClick={() => openEditModal(banner)}
                  className="text-indigo-600 hover:text-indigo-900"
                >
                  Sửa
                </button>
                <button
                  onClick={() => openDeleteModal(banner.id!)}
                  className="text-red-600 hover:text-red-900"
                >
                  Xóa
                </button>
              </div>
            </div>
          ))
        ) : (
          <div className="col-span-full text-center py-10 bg-white rounded-lg shadow">
            <p className="text-gray-500">Không có dữ liệu banner</p>
          </div>
        )}
      </div>

      {/* Add/Edit Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-10 overflow-y-auto">
          <div className="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
            <div
              className="fixed inset-0 transition-opacity"
              aria-hidden="true"
              onClick={() => setIsModalOpen(false)}
            >
              <div className="absolute inset-0 bg-gray-500 opacity-75"></div>
            </div>

            <span
              className="hidden sm:inline-block sm:align-middle sm:h-screen"
              aria-hidden="true"
            >
              &#8203;
            </span>

            <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
              <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                <div className="sm:flex sm:items-start">
                  <div className="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left w-full">
                    <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                      {editingBanner ? "Chỉnh sửa" : "Thêm"} Banner
                    </h3>
                    <div className="space-y-4">
                      <div>
                        <label
                          htmlFor="url"
                          className="block text-sm font-medium text-gray-700"
                        >
                          URL ảnh banner
                        </label>
                        <input
                          type="text"
                          id="url"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                          placeholder="https://example.com/image.jpg"
                          value={newBanner.url}
                          onChange={(e) =>
                            setNewBanner({
                              ...newBanner,
                              url: e.target.value,
                            })
                          }
                        />
                      </div>
                      
                      {newBanner.url && (
                        <div>
                          <p className="block text-sm font-medium text-gray-700 mb-2">
                            Xem trước
                          </p>
                          <div className="border border-gray-300 rounded-md overflow-hidden">
                            <img 
                              src={newBanner.url} 
                              alt="Banner preview" 
                              className="w-full h-auto"
                              onError={(e) => {
                                (e.target as HTMLImageElement).src = "https://via.placeholder.com/640x360?text=Invalid+Image+URL";
                              }}
                            />
                          </div>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>
              <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
                <button
                  type="button"
                  className="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:ml-3 sm:w-auto sm:text-sm"
                  onClick={handleSave}
                >
                  {editingBanner ? "Cập nhật" : "Thêm"}
                </button>
                <button
                  type="button"
                  className="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
                  onClick={() => setIsModalOpen(false)}
                >
                  Hủy
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {isDeleteModalOpen && (
        <div className="fixed inset-0 z-10 overflow-y-auto">
          <div className="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
            <div
              className="fixed inset-0 transition-opacity"
              aria-hidden="true"
              onClick={() => setIsDeleteModalOpen(false)}
            >
              <div className="absolute inset-0 bg-gray-500 opacity-75"></div>
            </div>

            <span
              className="hidden sm:inline-block sm:align-middle sm:h-screen"
              aria-hidden="true"
            >
              &#8203;
            </span>

            <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
              <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                <div className="sm:flex sm:items-start">
                  <div className="mx-auto flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-full bg-red-100 sm:mx-0 sm:h-10 sm:w-10">
                    <svg
                      className="h-6 w-6 text-red-600"
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                      aria-hidden="true"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth="2"
                        d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                      />
                    </svg>
                  </div>
                  <div className="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left">
                    <h3
                      className="text-lg leading-6 font-medium text-gray-900"
                      id="modal-title"
                    >
                      Xác nhận xóa
                    </h3>
                    <div className="mt-2">
                      <p className="text-sm text-gray-500">
                        Bạn có chắc chắn muốn xóa banner này? Hành động này
                        không thể hoàn tác.
                      </p>
                    </div>
                  </div>
                </div>
              </div>
              <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
                <button
                  type="button"
                  className="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-red-600 text-base font-medium text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sm:ml-3 sm:w-auto sm:text-sm"
                  onClick={handleDelete}
                >
                  Xóa
                </button>
                <button
                  type="button"
                  className="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
                  onClick={() => setIsDeleteModalOpen(false)}
                >
                  Hủy
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}